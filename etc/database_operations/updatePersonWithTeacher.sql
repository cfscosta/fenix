select concat('update PERSON set PERSON.KEY_GRANT_OWNER = ',
	GRANT_OWNER.ID_INTERNAL,' where PERSON.ID_INTERNAL = ', 
	GRANT_OWNER.KEY_PERSON, ';') 
as "" from GRANT_OWNER where GRANT_OWNER.KEY_PERSON IS NOT NULL;