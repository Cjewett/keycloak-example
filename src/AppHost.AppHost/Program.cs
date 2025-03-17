var builder = DistributedApplication.CreateBuilder(args);

var password = builder.AddParameter("password", secret: true);
var sql = builder.AddSqlServer("sql", password: password, port: 1433)
    .WithDataVolume()
    .WithContainerRuntimeArgs("--name", "mssql", "-p", "1433:1433");
var db = sql.AddDatabase("master");

var databaseInitialization = builder.AddProject<Projects.DatabaseInitialization>("db-init")
    .WithReference(db)
    .WaitFor(db);

var keycloak = builder.AddKeycloak("keycloak", 8080)
    .WithImageRegistry("")
    .WithImage("keycloak-override")
    .WithImageTag("latest")
    .WithEnvironment("KC_BOOTSTRAP_ADMIN_USERNAME", "admin")
    .WithEnvironment("KC_BOOTSTRAP_ADMIN_PASSWORD", "password")
    .WithDataVolume()
    .WaitForCompletion(databaseInitialization)
    .WithContainerName("keycloak")
    .WithRealmImport("test-realm.json");

var api = builder.AddProject<Projects.Api>("api")
    .WithReference(keycloak)
    .WaitFor(keycloak);

builder.Build().Run();
