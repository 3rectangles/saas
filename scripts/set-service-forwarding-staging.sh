#!/bin/bash
cp scripts/ssh-config-gitpod ~/.ssh/config

# set the namespace name
NAMESPACE_NAME="barraiser-backend-staging"

# get a list of all services in the namespace
SERVICE_LIST=$(aws servicediscovery list-services --filters Name=NAMESPACE_ID,Values=ns-gmztlyu63vvats4k --output text --query "Services[].Name")

# loop over each service in the list
for SERVICE_NAME in $SERVICE_LIST; do
    echo $SERVICE_NAME
    # get the IP and port of the first instance of the service
    INSTANCE_INFO=$(aws servicediscovery discover-instances --namespace-name $NAMESPACE_NAME --service-name $SERVICE_NAME);

    # extract the IP and port from the instance info
    INSTANCE_IP=$(echo $INSTANCE_INFO | jq -r '.Instances[0].Attributes.AWS_INSTANCE_IPV4')
    INSTANCE_PORT=$(echo $INSTANCE_INFO | jq -r '.Instances[0].Attributes.AWS_INSTANCE_PORT // "8080"')

    echo $INSTANCE_IP
    echo $INSTANCE_PORT

    # set up iptables rules to redirect traffic to the instance to the same port on localhost
    sudo iptables -t nat -I OUTPUT --dst $INSTANCE_IP -p tcp --dport $INSTANCE_PORT -j REDIRECT --to-ports $INSTANCE_PORT

    # add a LocalForward entry to the ssh config file for the service
    printf "\nLocalForward localhost:$INSTANCE_PORT $INSTANCE_IP:$INSTANCE_PORT # $SERVICE_NAME" >> ~/.ssh/config
done

# open ssh tunnel with localforwards in config
ssh -o "StrictHostKeyChecking=no" estunnel -fNTM
