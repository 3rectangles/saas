environment=$1
version=$(date +%Y.%m.%d.%H%M)

echo "Running deploy stage for environment:$1"
myuser=$(whoami)
echo "User : $myuser"

jar_to_be_packaged=$(ls|grep -i "interviewing.*.jar")
echo "jar to be packaged : $jar_to_be_packaged"


s3_bucket=""
procfile="Procfile-$environment-$version"
application_name=""

if [ "$environment" == "staging" ];then
        s3_bucket="elasticbeanstalk-ap-south-1-969111487786"
        printf "web: java -Xms1024m -Xmx2048m -jar -Dspring.profiles.active=staging "$jar_to_be_packaged"" > "$procfile"
        application_name="barraiser"
fi
if [ "$environment" == "acceptance" ];then
        echo "Setup s3 bucket for acceptance deployment"
        s3_bucket="elasticbeanstalk-ap-south-1-969111487786"
        application_name="backend-staging-ap-south-1"
        printf "web: java -Xms1024m -Xmx2048m -jar -Dspring.profiles.active=acceptance "$jar_to_be_packaged"" > "$procfile"
fi
if [ "$environment" == "app-production-v2" ];then
        echo "Setup s3 bucket for prod deployment"
        s3_bucket="elasticbeanstalk-ap-south-1-969111487786"
        application_name="backend-production-ap-south-1"
        printf "web: java -Xms1024m -Xmx2048m -jar -Dspring.profiles.active=prod "$jar_to_be_packaged"" > "$procfile"
fi





#Packaging zip to create artifact
artifact="interviewing-$environment-$version.zip"
cp "$procfile" Procfile
echo "Executing command : zip -r interviewing-$environment-$version.zip "$jar_to_be_packaged" Procfile .platform"
zip -r "$artifact" "$jar_to_be_packaged" Procfile .platform

#myuser=$(whoami)
#echo "User : $myuser"
#Uploading artifact to s3
echo "Executing command : aws s3 cp "$artifact" s3://elasticbeanstalk-ap-south-1-969111487786/$artifact"
aws s3 cp "$artifact" s3://elasticbeanstalk-ap-south-1-969111487786/"$artifact"

echo "Deploying for environment : $environment"


echo "Creating application version for artifact : $artifact"
aws elasticbeanstalk create-application-version --application-name "$application_name" --version-label "$artifact"  --source-bundle S3Bucket="$s3_bucket",S3Key="$artifact"

echo "Deploying"
aws elasticbeanstalk update-environment --environment-name "$environment" --version-label "$artifact"
echo "Deployment Triggered Successfully"

#Health check
ExpectedStatus="UP"
actualStatus="DOWN"
sleep 1m
for i in {1..10}
do
    if [[ $environment == "staging" ]];
    then
        status=$(echo `curl -X GET https://api.staging.barraiser.in/actuator/health` | tr ":" "\n" | tail -1)
    fi
    #Todo - Version check for prod deployment
    if [[ $environment == "app-production-v2" ]];
    then
        status=$(echo `curl -X GET https://api.barraiser.com/actuator/health/` | tr ":" "\n" | tail -1)
    fi
    if [[ $status == *"UP"* ]];
        then   
            echo "Server is UP"
            actualStatus="UP"
            echo "STATUS :"$actualStatus
            exit 0
            break
        else
            echo "Server is not UP!! Polling the status"
            echo "STATUS :"$actualStatus
            sleep 1m
        fi
done
if [[ $actualStatus != $ExpectedStatus ]];
    then
    exit 1 
fi
echo "Deployment Successful"
