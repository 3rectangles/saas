echo 'installing docker'
brew install docker
echo 'installing libpg'
brew install libpq
# ZSH
echo 'export PATH="/home/linuxbrew/.linuxbrew/opt/libpq/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
# Bash
echo 'export PATH="/home/linuxbrew/.linuxbrew/opt/libpq/bin:$PATH"' >> ~/.bash_profile
source ~/.bash_profile
rm ~/.pgpass
touch ~/.pgpass
echo 'staging.czggcjhujenw.ap-south-1.rds.amazonaws.com:5432:barraiser:barraiser:Bh5RQN>mO1@t' >> ~/.pgpass
chmod 600 ~/.pgpass
echo 'dump staging db'
rm  /workspace/abc-dump.sql
pg_dump -U barraiser -h staging.czggcjhujenw.ap-south-1.rds.amazonaws.com -C barraiser > /workspace/abc-dump.sql
echo 'dump staging db'
docker-compose up -d
export PGPASSWORD=barraiser1234
sleep 10
echo 'drop local db barraiser'
dropdb -h localhost -p 5432 -U barraiser barraiser -f
sleep 10
echo 'create local db barraiser'
createdb -h localhost -p 5432 -U barraiser barraiser
sleep 10
echo 'import data in barraiser db'
psql -h localhost -p 5432 -U  barraiser  -d barraiser -f /workspace/abc-dump.sql