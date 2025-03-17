using Microsoft.Data.SqlClient;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Hosting;

namespace DatabaseInitialization;

public class Program
{
    public static int Main(string[] args)
    {
        var builder = Host.CreateApplicationBuilder();
        builder.AddSqlServerClient(connectionName: "master");
        var databaseConnectionString = builder.Configuration.GetConnectionString("master");

        try
        {
            using var sqlConnection = new SqlConnection(databaseConnectionString);
            sqlConnection.Open();

            string createTableQuery = @"
                IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='customers' AND xtype='U')
                CREATE TABLE customers (
                    id INT IDENTITY(1,1) PRIMARY KEY,
                    username NVARCHAR(255) UNIQUE NOT NULL,
                    password NVARCHAR(255) NOT NULL
                );";

            using (var command = new SqlCommand(createTableQuery, sqlConnection))
            {
                command.ExecuteNonQuery();
            }

            string insertUserQuery = @"
                IF NOT EXISTS (SELECT 1 FROM customers WHERE username = 'customer')
                INSERT INTO customers (username, password) VALUES ('customer', 'password');";

            using (var command = new SqlCommand(insertUserQuery, sqlConnection))
            {
                command.ExecuteNonQuery();
            }
        }
        catch (Exception ex)
        {
            Console.Error.WriteLine($"Database initialization failed: {ex.Message}");
            return 1;
        }

        return 0;
    }
}