ALTER TABLE affiliates
ADD CONSTRAINT uq_affiliates_document UNIQUE (document);

ALTER TABLE credits
ADD CONSTRAINT fk_credits_affiliate FOREIGN KEY (affiliate_id) REFERENCES affiliates(id);
