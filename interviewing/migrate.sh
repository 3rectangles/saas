env=$1

host=""
user=""
password=""

cd ./interviewing

if [ "$env" == "local" ];then
    host="jdbc:postgresql://localhost:5432/barraiser"
    user="root"
    password="root"
fi
if [ "$env" == "staging" ];then
    secret=`aws secretsmanager get-secret-value --secret-id newstaging/barraiser`
    host=jdbc:postgresql://`echo $secret | sed "s/^.*host.....//" | sed 's/.".*$//'`:5432/barraiser
    user=`echo $secret | sed "s/^.*username.....//" | sed 's/.".*$//'`
    password=`echo $secret | sed "s/^.*password.....//" | sed 's/.".*$//'`
fi
if [ "$env" == "prod" ];then
    secret=`aws secretsmanager get-secret-value --secret-id prod/soulchild`
    host=jdbc:postgresql://`echo $secret | sed "s/^.*host.....//" | sed 's/.".*$//'`:5432/barraiser
    user=`echo $secret | sed "s/^.*username.....//" | sed 's/.".*$//'`
    password=`echo $secret | sed "s/^.*password.....//" | sed 's/.".*$//'`
fi

mvn -Dflyway.url="$host" -Dflyway.user=$user -Dflyway.schemas=public -Dflyway.password="$password" -Dflyway.outOfOrder="true" -Dflyway.validateOnMigrate="false" flyway:migrate
