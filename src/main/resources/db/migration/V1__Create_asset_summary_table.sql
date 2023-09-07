create sequence asset_seq;
create table ASSET (
    ID SERIAL not null PRIMARY KEY,
    NAME varchar(100) not null,
    DATE DATE not null,
    CREDIT DOUBLE PRECISION not null,
    DEBIT DOUBLE PRECISION not null
);