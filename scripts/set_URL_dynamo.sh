dynamo_key=$(git config user.email)_backend
url_workspace=$(gp url 5000)
echo setting key $dynamo_key with url: $url_workspace
aws dynamodb put-item \
    --table-name gitpod_workspace_user_repo \
    --item '{"user_repo": {"S": "'"$dynamo_key"'"}, "other_attribute": {"S": "'"$url_workspace"'"}}'