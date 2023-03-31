ALTER TABLE product
ADD COLUMN if not exists auto_processing BOOLEAN DEFAULT FALSE NOT NULL,
ADD COLUMN if not exists type_credit VARCHAR(11) NOT NULL default 'CONSUMER';