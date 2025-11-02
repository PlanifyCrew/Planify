import { ApolloServer } from "apollo-server";
import { readFileSync } from "fs";
import path from "path";
import resolvers from "./resolver.js";

// Pfad zum Schema
const schemaPath = path.join(__dirname, "resources/schema.graphqls");
const typeDefs = readFileSync(schemaPath, { encoding: "utf-8" });

// Apollo Server erstellen
const server = new ApolloServer({
  typeDefs,
  resolvers,
});

// Server starten
const PORT = process.env.PORT || 4000;
server.listen({ port: PORT }).then(({ url }) => {
  console.log(`GraphQL server ready at ${url}`);
});
