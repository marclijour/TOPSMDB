#!/bin/bash

echo "connect 'jdbc:derby:/var/lib/tomcat7/topsDB;user=tops;password=900bst';" > getBlankEmails.sql
echo "select * from members where email ='';" >> getBlankEmails.sql

java -cp derby.jar:derbytools.jar -Dij.outfile=getBlankEmails.log org.apache.derby.tools.ij  getBlankEmails.sql

