ALTER TABLE T_TASK ALTER column DAUER TYPE integer USING DAUER::INTEGER;
ALTER TABLE T_TASK ALTER column DATUM TYPE timestamp;
ALTER TABLE T_TASK ADD column CALENDAR_ID VARCHAR(100);
ALTER TABLE T_TASK ADD column CALENDAR_SEQUENCE integer;