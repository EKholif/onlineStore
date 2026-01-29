import mysql.connector

def count_users(tenant_id):
    try:
        conn = mysql.connector.connect(
            host="localhost",
            user="root",
            password="0000",
            database="shop"
        )
        cursor = conn.cursor()
        
        # Count users for tenant 4
        # Note: Users might be in 'users' table (based on User.java @Table(name = "'user'"))
        # and filtered by tenant_id
        
        query = "SELECT COUNT(*) FROM user WHERE tenant_id = %s"
        cursor.execute(query, (tenant_id,))
        
        result = cursor.fetchone()
        print(f"Total Users for Tenant {tenant_id}: {result[0]}")
        
        # Also let's print the IDs to compare with folders
        query_ids = "SELECT id FROM user WHERE tenant_id = %s"
        cursor.execute(query_ids, (tenant_id,))
        ids = [row[0] for row in cursor.fetchall()]
        print(f"User IDs: {ids}")
        
        cursor.close()
        conn.close()
        
    except mysql.connector.Error as err:
        print(f"Error: {err}")

if __name__ == "__main__":
    count_users(4)
