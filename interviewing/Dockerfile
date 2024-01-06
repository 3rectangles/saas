FROM ubuntu:22.04
RUN apt-get update -y
RUN export TZ=Etc/UTC
RUN apt install -y tzdata
RUN apt install -y postgresql
RUN apt install -y postgresql-contrib postgresql-client
COPY ./staging_dump.dump /db/
COPY ./start-up.sh /db/
RUN chmod +x /db/start-up.sh
RUN apt install sudo
ENTRYPOINT ["/db/start-up.sh"]
