cp target/onboarding-1.0.0.jar onboarding.jar

version=$(date +%Y.%m.%d.%H%M)
cp StagingProcfile Procfile
zip -r onboarding-$version.zip onboarding.jar Procfile .platform

cp ProdProcfile Procfile
zip -r onboarding-prod-$version.zip onboarding.jar Procfile .platform

myuser=$(whoami)
echo "User : $myuser"
aws s3 cp onboarding-$version.zip s3://elasticbeanstalk-ap-south-1-969111487786/onboarding-$version.zip
