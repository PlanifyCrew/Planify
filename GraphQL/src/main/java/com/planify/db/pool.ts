import pkg from "pg";
const { Pool } = pkg;

// Heroku und lokale Nutzung
const pool = new Pool({
  connectionString: process.env.DATABASE_URL || "postgresql://postgres:Slay123@localhost:5432/PlanifyDB",
  ssl: process.env.DATABASE_URL ? { rejectUnauthorized: false } : false,
});

export default pool;
