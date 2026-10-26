-- Development-only sample data. Run only against a local development database.
IF DB_NAME() NOT IN (N'BookCrudDev', N'BookCrudLocal')
    THROW 50001, 'Refusing to seed a database not named BookCrudDev or BookCrudLocal.', 1;

IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE title = N'Clean Code')
BEGIN
    INSERT INTO dbo.books (title, description, stock, is_shown, price)
    VALUES
      (N'Clean Code', N'A practical book about writing maintainable software.', N'AVAILABLE', 1, 39.99),
      (N'Designing Data-Intensive Applications', N'Data systems concepts and trade-offs.', N'BOOKED', 1, 54.50),
      (N'Effective Java', N'Java programming best practices.', N'OUT_OF_STOCK', 0, 44.00);
END;
