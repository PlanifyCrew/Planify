"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const pg_1 = __importDefault(require("pg"));
const { Pool } = pg_1.default;
// Heroku und lokale Nutzung
const pool = new Pool({
    connectionString: process.env.DATABASE_URL || "postgres://udfs2mqik8ah2c:p8e291e59de3810e593f30e436d70c25ed9ee5d3498fa69d864195d7cefec8920@cfcojm7sp9tfip.cluster-czz5s0kz4scl.eu-west-1.rds.amazonaws.com:5432/d44sbjo012nqhj",
    ssl: process.env.DATABASE_URL ? { rejectUnauthorized: false } : false,
});
exports.default = pool;
