"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const pool_js_1 = __importDefault(require("./db/pool.js"));
const uuid_1 = require("uuid");
const node_fetch_1 = __importDefault(require("node-fetch"));
const resolvers = {
    Query: {
        getEvents: async (_, { token, startDate, endDate }) => {
            // Token prüfen -> UserId abrufen
            const { rows: userRows } = await pool_js_1.default.query("SELECT user_id FROM users WHERE token=$1 AND expiry_date > CURRENT_TIMESTAMP", [token]);
            if (!userRows.length)
                throw new Error("Invalid token");
            const userId = userRows[0].id;
            const { rows: events } = await pool_js_1.default.query("SELECT * FROM events WHERE user_id=$1 AND date BETWEEN $2 AND $3", [userId, startDate, endDate]);
            return events;
        },
        getEvent: async (_, { token, event_id }) => {
            // Token prüfen 
            const { rows: userRows } = await pool_js_1.default.query("SELECT user_id FROM users WHERE token=$1 AND expiry_date > CURRENT_TIMESTAMP", [token]);
            if (!userRows.length)
                throw new Error("Invalid token");
            const { rows: eventRows } = await pool_js_1.default.query("SELECT * FROM events WHERE event_id=$1", [event_id]);
            const { rows: tnListeRows } = await pool_js_1.default.query("SELECT p.*, u.email FROM participants p JOIN users u ON p.user_id = u.user_id WHERE event_id=$1", [event_id]);
            if (!eventRows.length)
                throw new Error("Event not found");
            const kalenderItem = {
                ...eventRows[0], // Einzelnes Event-Objekt
                tnListe: tnListeRows // Liste der Teilnehmer
            };
            return kalenderItem;
        }
    },
    Mutation: {
        login: async (_, { email, password }) => {
            const { rows } = await pool_js_1.default.query("SELECT user_id FROM users WHERE email=$1 AND password=$2", [email, password]);
            if (!rows.length)
                return { token: "OFF", status: "0" };
            const token = (0, uuid_1.v4)(); // zufälliger Token
            await pool_js_1.default.query("UPDATE users SET token=$1, expiry_date = CURRENT_TIMESTAMP + INTERVAL '1 hour' WHERE user_id=$2", [token, rows[0].id]);
            return { token, status: "200" };
        },
        logout: async (_, { token }) => {
            const { rowCount } = await pool_js_1.default.query("UPDATE users SET token='OFF', expiry_date = NULL WHERE token=$1", [token]);
            if (!rowCount)
                return { message: "User could not be logged out." };
            return { message: "User logged out." };
        },
        createUser: async (_, { name, email, password }) => {
            await pool_js_1.default.query("INSERT INTO users(name, email, password, token) VALUES($1,$2,$3,'OFF')", [name, email, password]);
            return { message: "User created." };
        },
        addEvent: async (_, { token, event, tnListe }) => {
            const client = await pool_js_1.default.connect();
            try {
                await client.query("BEGIN");
                // UserId prüfen
                const { rows: userRows } = await client.query("SELECT user_id FROM users WHERE token=$1 AND expiry_date > CURRENT_TIMESTAMP", [token]);
                if (!userRows.length) {
                    await client.query("ROLLBACK");
                    return { message: "Invalid token" };
                }
                const userId = userRows[0].id;
                //Event einfügen
                const { rows: newEvent } = await client.query("INSERT INTO events(name,date,description,start_time,end_time) VALUES($1,$2,$3,$4,$5) RETURNING event_id", [event.name, event.date, event.description, event.startTime, event.endTime]);
                const eventId = newEvent[0].id;
                // Organisator als Teilnehmer einfügen
                await pool_js_1.default.query("INSERT INTO participants(event_id,user_id,role,status) VALUES ($1,$2,$3,$4)", [eventId, userId, "Organisator", "erstellt"]);
                // Teilnehmer hinzufügen
                const { rows: tnRows } = await client.query("SELECT user_id FROM users WHERE email=ANY($1::text[])", [tnListe]);
                // Teilnehmer auf einmal einfügen
                if (tnRows.length > 0) {
                    const insertValues = tnRows
                        .map((u, i) => `($1, $${i + 2}, 'Teilnehmer', 'ausstehend')`)
                        .join(", ");
                    await client.query(`INSERT INTO participants (event_id, user_id, role, status) VALUES ${insertValues}`, [eventId, ...tnRows.map((u) => u.user_id)]);
                }
                await client.query("COMMIT");
                return { message: "Event created." };
            }
            catch (error) {
                await client.query("ROLLBACK");
                console.error("Fehler beim Erstellen des Events:", error);
                throw error;
            }
            finally {
                client.release();
            }
        },
        updateEvent: async (_, { event_id, token, event, tnListe }) => {
            const client = await pool_js_1.default.connect();
            try {
                await client.query("BEGIN");
                // UserId prüfen
                const { rows: userRows } = await client.query("SELECT user_id FROM users WHERE token=$1 AND expiry_date > CURRENT_TIMESTAMP", [token]);
                if (!userRows.length)
                    return { message: "Invalid token" };
                const userId = userRows[0].id;
                //Event updaten
                await client.query("UPDATE events(name,date,description,start_time,end_time) VALUES($1,$2,$3,$4,$5)", [event.name, event.date, event.description, event.startTime, event.endTime]);
                // Alle aktuellen Teilnehmer für das Event holen
                const { rows: currentParticipants } = await client.query("SELECT user_id FROM participants WHERE event_id = $1", [event_id]);
                const currentUserIds = currentParticipants.map((row) => row.user_id);
                // IDs der neuen Teilnehmer anhand der E-Mails holen
                const { rows: newUsers } = await client.query("SELECT user_id, email FROM users WHERE email = ANY($1::text[])", [tnListe]);
                const newUserIds = newUsers.map((u) => u.user_id);
                // Differenzen berechnen
                const toAdd = newUserIds.filter((id) => !currentUserIds.includes(id));
                const toRemove = currentUserIds.filter((id) => !newUserIds.includes(id));
                // Neue Teilnehmer hinzufügen
                for (const userId of toAdd) {
                    await client.query("INSERT INTO participants (event_id, user_id, role, status) VALUES ($1, $2, $3, $4)", [event_id, userId, "Teilnehmer", "ausstehend"]);
                }
                // Alte Teilnehmer entfernen
                for (const userId of toRemove) {
                    await client.query("DELETE FROM participants WHERE event_id = $1 AND user_id = $2", [event_id, userId]);
                }
                await client.query("COMMIT");
                console.log("Teilnehmer erfolgreich aktualisiert");
            }
            catch (error) {
                await client.query("ROLLBACK");
                console.error("Fehler beim Aktualisieren der Teilnehmer:", error);
                throw error;
            }
            finally {
                client.release();
            }
        }
        // und deleteEvent würden analog implementiert
    },
    deleteEvent: async (_, { eventId, token }) => {
        const { rows: userRows } = await pool_js_1.default.query("SELECT user_id FROM users WHERE token=$1 AND expiry_date > CURRENT_TIMESTAMP", [token]);
        if (!userRows.length)
            throw new Error("Invalid token");
        const userId = userRows[0].user_id;
        const { rowCount } = await pool_js_1.default.query("DELETE FROM events WHERE event_id=$1", [eventId]);
        if (rowCount === 0)
            return { message: "Event not found or not allowed" };
        const { rowCount: countPart } = await pool_js_1.default.query("DELETE FROM participants WHERE event_id=$1", [eventId]);
        if (countPart === 0)
            return { message: "Couldn't delete events from participants" };
        return { message: "Event deleted" };
    },
    sendEmail: async (_, { token, event, tnListe }) => {
        const { rows: userRows } = await pool_js_1.default.query("SELECT user_id FROM users WHERE token=$1 AND expiry_date > CURRENT_TIMESTAMP", [token]);
        if (!userRows.length)
            throw new Error("Invalid token");
        const brevoUrl = "https://email-server-planify-slay-884dac5f7888.herokuapp.com/sendEmail";
        for (const email of tnListe) {
            const body = {
                email: email,
                name: "Teilnehmer",
                subject: "Willkommen zum Event!",
                htmlContent: `<p>Hallo ${email}, schön dass du beim Event <strong>${event.name}</strong> am ${event.date} dabei bist!</p>`
            };
            try {
                const response = await (0, node_fetch_1.default)(brevoUrl, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(body)
                });
                const result = await response.json();
                console.log(`E-Mail an ${email}:`, response.status, result);
            }
            catch (error) {
                console.error(`Fehler beim Senden an ${email}:`, error);
            }
        }
        return { success: true, message: "E-Mails wurden versendet." };
    }
};
exports.default = resolvers;
