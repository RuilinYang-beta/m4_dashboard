bookings
(dossierId , bookingId PRIMARY KEY,
orderState , bookingIdentifier,
containerNumber , createdOn,
createdBy , referenceDate,
containerType , teu, shippingCompany,
shippingCompanyId , shippingCompanyScac,
nettoWeight , brutoWeight , customer);

locations
(locationId PRIMARY KEY, type,
name , longitude , latitude,
color , globalsearch , aliases,
terminalCode , unlo);

address
(locationId PRIMARY KEY, FOREIGN KEY REF (locations.locationId),
street , number , postfix,
postalCode , city , country);
