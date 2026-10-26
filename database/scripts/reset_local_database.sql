-- DANGEROUS: local-development-only utility. Do not run against shared or production DBs.
IF DB_NAME() NOT IN (N'BookCrudDev', N'BookCrudLocal')
    THROW 50002, 'Refusing to reset a database not named BookCrudDev or BookCrudLocal.', 1;

DELETE FROM dbo.books;
DBCC CHECKIDENT ('dbo.books', RESEED, 0);
