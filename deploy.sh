#!/usr/bin/env bash

# List of Clients to which the executable should be deployed"
# declare as associative array
declare -A clients
clients[pi]="192.168.1.39"
clients[odroid]="192.168.1.59"

# Deploy target name
LOCAL_TARGET_DIR="target"
LOCAL_TARGET_NAME="sync-client-0.1-SNAPSHOT-client.zip"
DEPLOY_TARGET="sync-client.zip"
UNZIPPED_DEPLOY_TARGET="sync-client-0.1-SNAPSHOT"
LOCAL_PUBLIC_KEY="$LOCAL_TARGET_DIR/sync-client-0.1-SNAPSHOT/deploy/p2psync_rsa.pub"
LOCAL_PRIVATE_KEY="$LOCAL_TARGET_DIR/sync-client-0.1-SNAPSHOT/deploy/p2psync_rsa"

echo "Starting to deploy $LOCAL_TARGET_DIR/$LOCAL_TARGET_NAME to ~/$DEPLOY_TARGET"
echo "---------------------------------------------------------------------------"
echo ""

echo "Unzip target"
echo "------------"
unzip ${LOCAL_TARGET_DIR}/${LOCAL_TARGET_NAME} -d "$LOCAL_TARGET_DIR/sync-client-0.1-SNAPSHOT"
echo ""

for entry in "${!clients[@]}"; do
  echo "Starting to deploy snapshot to Client $entry@"${clients[$entry]}
  echo "------------------------------------------------------------"
  echo "Killing already running jars"
  ssh -i ${LOCAL_PRIVATE_KEY} ${entry}@${clients[$entry]} "pkill -f 'java -jar'"
  echo ""
  echo "Remove old archive and directory on remote if existing: rm ~/$DEPLOY_TARGET; rm -rf ~/$UNZIPPED_DEPLOY_TARGET"
  ssh -i ${LOCAL_PRIVATE_KEY} ${entry}@${clients[$entry]} "rm ~/$DEPLOY_TARGET; rm -rf ~/$UNZIPPED_DEPLOY_TARGET"
  echo ""
  echo "Deploy ${LOCAL_TARGET_DIR}/${LOCAL_TARGET_NAME} to Client with IP: ${clients[$entry]}"
  scp -i ${LOCAL_PRIVATE_KEY} ${LOCAL_TARGET_DIR}/${LOCAL_TARGET_NAME} ${entry}@${clients[$entry]}:~/${DEPLOY_TARGET}
  echo ""
  echo "Unzipping on remote..."
  ssh -i ${LOCAL_PRIVATE_KEY} ${entry}@${clients[$entry]} "unzip ~/$DEPLOY_TARGET -d ~/$UNZIPPED_DEPLOY_TARGET"
  echo ""
  echo "Client $entry@${clients[$entry]} deployed successfully"
  echo ""
  sleep 1
done

echo "Deployed to all Clients"