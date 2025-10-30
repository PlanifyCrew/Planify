import pool from "./db/pool.js";
import { v4 as uuidv4 } from "uuid";


const resolvers = {
  Query: {
    getEvents: async (_: any, { token, startDate, endDate }: any) => {
      // Token prüfen -> UserId abrufen
      const { rows: userRows } = await pool.query(
        "SELECT user_id FROM users WHERE token=$1",
        [token]
      );
      if (!userRows.length) throw new Error("Invalid token");

      const userId = userRows[0].id;

      const { rows: events } = await pool.query(
        "SELECT * FROM events WHERE user_id=$1 AND date BETWEEN $2 AND $3",
        [userId, startDate, endDate]
      );
      return events;
    },
  },

  Mutation: {
    login: async (_: any, { email, password }: any) => {
      const { rows } = await pool.query(
        "SELECT user_id FROM users WHERE email=$1 AND password=$2",
        [email, password]
      );
      if (!rows.length) return { token: "OFF", status: "0" };

      const token = uuidv4(); // zufälliger Token
      await pool.query("UPDATE users SET token=$1 WHERE user_id=$2", [token, rows[0].id]);
      return { token, status: "200" };
    },

    logout: async (_: any, { token }: any) => {
      const { rowCount } = await pool.query("UPDATE users SET token='OFF' WHERE token=$1", [token]);
      if (!rowCount) return { message: "User could not be logged out." };
      return { message: "User logged out." };
    },

    createUser: async (_: any, { name, email, password }: any) => {
      await pool.query(
        "INSERT INTO users(name, email, password, token) VALUES($1,$2,$3,'OFF')",
        [name, email, password]
      );
      return { message: "User created." };
    },

    addEvent: async (_: any, { token, event, tnListe }: any) => {
      const client = await pool.connect();

      try {
        await client.query("BEGIN");
        // UserId prüfen
        const { rows: userRows } = await client.query(
          "SELECT user_id FROM users WHERE token=$1",
          [token]
        );
        if (!userRows.length) {
          await client.query("ROLLBACK");
          return { message: "Invalid token" };
        }
        const userId = userRows[0].id;

        //Event einfügen
        const { rows: newEvent } = await client.query(
          "INSERT INTO events(name,date,description,start_time,end_time) VALUES($1,$2,$3,$4,$5) RETURNING event_id",
          [event.name, event.date, event.description, event.startTime, event.endTime]
        );
        const eventId = newEvent[0].id;

        // Organisator als Teilnehmer einfügen
        await pool.query(
          "INSERT INTO participants(event_id,user_id,role,status) VALUES ($1,$2,$3,$4)",
          [eventId, userId, "Organisator", "erstellt"]
        );

        // Teilnehmer hinzufügen
        const { rows: tnRows } = await client.query(
          "SELECT user_id FROM users WHERE email=ANY($1::text[])",
          [tnListe]
        );

        // Teilnehmer auf einmal einfügen
        if (tnRows.length > 0) {
          const insertValues = tnRows
            .map(
              (u, i) =>
                `($1, $${i + 2}, 'Teilnehmer', 'ausstehend')`
            )
            .join(", ");

          await client.query(
            `INSERT INTO participants (event_id, user_id, role, status) VALUES ${insertValues}`,
            [eventId, ...tnRows.map((u) => u.user_id)]
          );
        }

        await client.query("COMMIT");
        return { message: "Event created." };
      } catch (error) {
        await client.query("ROLLBACK");
        console.error("Fehler beim Erstellen des Events:", error);
        throw error;
      } finally {
        client.release();
      }
    },

    updateEvent: async (_: any, { event_id, token, event, tnListe }: any) => {
      const client = await pool.connect();

      try {
        await client.query("BEGIN");
      
        // UserId prüfen
        const { rows: userRows } = await client.query(
          "SELECT user_id FROM users WHERE token=$1",
          [token]
        );
        if (!userRows.length) return { message: "Invalid token" };
        const userId = userRows[0].id;

        //Event updaten
        await client.query(
          "UPDATE events(name,date,description,start_time,end_time) VALUES($1,$2,$3,$4,$5)",
          [event.name, event.date, event.description, event.startTime, event.endTime]
        );

        // Alle aktuellen Teilnehmer für das Event holen
        const { rows: currentParticipants } = await client.query(
          "SELECT user_id FROM participants WHERE event_id = $1",
          [event_id]
        );

        const currentUserIds = currentParticipants.map((row) => row.user_id);

        // IDs der neuen Teilnehmer anhand der E-Mails holen
        const { rows: newUsers } = await client.query(
          "SELECT user_id, email FROM users WHERE email = ANY($1::text[])",
          [tnListe]
        );
        const newUserIds = newUsers.map((u) => u.user_id);

        // Differenzen berechnen
        const toAdd = newUserIds.filter((id) => !currentUserIds.includes(id));
        const toRemove = currentUserIds.filter((id) => !newUserIds.includes(id));

        // Neue Teilnehmer hinzufügen
        for (const userId of toAdd) {
          await client.query(
            "INSERT INTO participants (event_id, user_id, role, status) VALUES ($1, $2, $3, $4)",
            [event_id, userId, "Teilnehmer", "ausstehend"]
          );
        }

        // Alte Teilnehmer entfernen
        for (const userId of toRemove) {
          await client.query(
            "DELETE FROM participants WHERE event_id = $1 AND user_id = $2",
            [event_id, userId]
          );
        }

        await client.query("COMMIT");
        console.log("Teilnehmer erfolgreich aktualisiert");
      } catch (error) {
          await client.query("ROLLBACK");
          console.error("Fehler beim Aktualisieren der Teilnehmer:", error);
          throw error;
      } finally {
          client.release();
      }
    }

    // und deleteEvent würden analog implementiert
  },

  deleteEvent: async (_: any, { eventId, token }: any) => {
    const { rows: userRows } = await pool.query(
      "SELECT user_id FROM users WHERE token=$1",
      [token]
    );
    if (!userRows.length) throw new Error("Invalid token");

    const userId = userRows[0].user_id;

    const { rowCount } = await pool.query(
      "DELETE FROM events WHERE event_id=$1",
      [eventId]
    );

    if (rowCount === 0) return { message: "Event not found or not allowed" };

    const { rowCount: countPart } = await pool.query(
      "DELETE FROM participants WHERE event_id=$1",
      [eventId]
    );

    if (countPart === 0) return { message: "Couldn't delete events from participants" };

    return { message: "Event deleted" };
  },

};
export default resolvers;