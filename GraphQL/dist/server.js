"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const apollo_server_1 = require("apollo-server");
const fs_1 = require("fs");
const path_1 = __importDefault(require("path"));
const resolver_js_1 = __importDefault(require("./resolver.js"));
// Pfad zum Schema
const schemaPath = path_1.default.join(__dirname, "resources/schema.graphqls");
const typeDefs = (0, fs_1.readFileSync)(schemaPath, { encoding: "utf-8" });
// Apollo Server erstellen
const server = new apollo_server_1.ApolloServer({
    typeDefs,
    resolvers: resolver_js_1.default,
});
// Server starten
const PORT = process.env.PORT || 4000;
server.listen({ port: PORT }).then(({ url }) => {
    console.log(`GraphQL server ready at ${url}`);
});
