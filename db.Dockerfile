FROM postgres:13.2

ENV POSTGRES_DB=flashcards
ENV POSTGRES_USER=dev
ENV POSTGRES_PASSWORD=dev

COPY database/pg_schema.sql /docker-entrypoint-initdb.d/01_schema.sql
COPY database/pg_data_full.sql /docker-entrypoint-initdb.d/02_data.sql

EXPOSE 5432
CMD ["postgres"]