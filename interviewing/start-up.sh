service postgresql start
sudo -u postgres psql -c 'create database barraiser;'
sudo -u postgres pg_restore -d barraiser ./staging_dump.dump
