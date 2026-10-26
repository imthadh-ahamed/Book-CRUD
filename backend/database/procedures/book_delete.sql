-- Reference script. The matching Flyway migration is authoritative.
CREATE OR ALTER PROCEDURE dbo.Book_Delete
    @id BIGINT
AS
BEGIN
    SET NOCOUNT ON;
    DELETE FROM dbo.books WHERE id = @id;
    SELECT @@ROWCOUNT AS rows_affected;
END;
