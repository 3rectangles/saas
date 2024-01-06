environment=$1
echo "Deploying for environment : $environment" 

s3_bucket=""
if [ "$environment"=="dev" ];then
	s3_bucket="elasticbeanstalk-ap-south-1-969111487786"	
	environment="staging"
fi
	
latest_build=$(aws s3 ls $s3_bucket | tail -n 1 | grep -o 'onboarding.*zip')
echo "latest build is : $latest_build"


application_name="barraiser"
echo "Creating application version"
aws elasticbeanstalk create-application-version --application-name "$application_name" --version-label "$latest_build"  --source-bundle S3Bucket="$s3_bucket",S3Key="$latest_build" 

echo "Deploying"
aws elasticbeanstalk update-environment --environment-name "$environment" --version-label "$latest_build"
echo "Deployment Successful"
