#!/usr/bin/env bash
# Bootstrap script to create the Currency Exchange microservices application
# This script creates three Quarkus projects: currency, portfolio, and trades as well as a root `pom.xml`

set -e

# Color output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}======================================${NC}"
echo -e "${BLUE}Currency Exchange Bootstrap Script${NC}"
echo -e "${BLUE}======================================${NC}"

# Common properties
GROUP_ID="com.pluralsight.currencyexchange"
VERSION="1.0.0-SNAPSHOT"
QUARKUS_VERSION="3.28.5"

# ======================================
# Currency Service (gRPC)
# ======================================
echo -e "\n${GREEN}Creating Currency Service (gRPC)...${NC}"

quarkus create app ${GROUP_ID}:currency:${VERSION} \
  --maven \
  --package-name=${GROUP_ID}.currency \
  --name="Currency Exchange :: Currency" \
  --platform-bom=${QUARKUS_VERSION} \
  --java=21 \
  --code \
  --app-config="quarkus.http.port=8082,quarkus.grpc.server.port=8082,quarkus.grpc.server.host=0.0.0.0,quarkus.grpc.server.use-separate-server=false,quarkus.application.name=Currency Micro Service,quarkus.log.level=INFO,quarkus.log.category.\"${GROUP_ID}\".level=DEBUG,exchange-rates.fluctuation-factor=0.02,quarkus.grpc.server.enable-reflection-service=true,quarkus.container-image.group=currencyexchange,quarkus.container-image.name=currency,quarkus.container-image.tag=jvm"

cd currency

# Add extensions
quarkus ext add grpc
quarkus ext add smallrye-health
quarkus ext add container-image-docker

cd ..

echo -e "${GREEN}Currency service created successfully!${NC}"

# ======================================
# Trades Service (REST + Jackson)
# ======================================
echo -e "\n${GREEN}Creating Trades Service (REST)...${NC}"

quarkus create app ${GROUP_ID}:trades:${VERSION} \
  --maven \
  --package-name=${GROUP_ID}.trade \
  --name="Currency Exchange :: Trades" \
  --platform-bom=${QUARKUS_VERSION} \
  --java=21 \
  --code \
  --app-config="quarkus.http.port=8083,quarkus.application.name=Trading Micro Service,quarkus.log.level=INFO,quarkus.log.category.\"${GROUP_ID}\".level=DEBUG,quarkus.smallrye-openapi.info-title=Currency Exchange Trades API,quarkus.smallrye-openapi.info-version=1.0.0,quarkus.smallrye-openapi.info-description=REST API for executing USD-based currency exchange trades and retrieving trade history,quarkus.container-image.group=currencyexchange,quarkus.container-image.name=trades,quarkus.container-image.tag=jvm"

cd trades

# Add extensions
quarkus ext add rest
quarkus ext add rest-jackson
quarkus ext add smallrye-openapi
quarkus ext add smallrye-health
quarkus ext add container-image-docker

cd ..

echo -e "${GREEN}Trades service created successfully!${NC}"

# ======================================
# Portfolio Service (Web + REST + gRPC Client)
# ======================================
echo -e "\n${GREEN}Creating Portfolio Service (Web UI)...${NC}"

quarkus create app ${GROUP_ID}:portfolio:${VERSION} \
  --maven \
  --package-name=${GROUP_ID}.portfolio \
  --name="Currency Exchange :: Portfolio" \
  --platform-bom=${QUARKUS_VERSION} \
  --java=21 \
  --code \
  --app-config="quarkus.http.port=8080,quarkus.application.name=Portfolio Service,quarkus.grpc.server.use-separate-server=false,quarkus.log.level=INFO,quarkus.log.category.\"${GROUP_ID}\".level=DEBUG,quarkus.rest-client.trades.url=http://localhost:8083,quarkus.grpc.clients.currency.host=localhost,quarkus.grpc.clients.currency.port=8082,quarkus.container-image.group=currencyexchange,quarkus.container-image.name=portfolio,quarkus.container-image.tag=jvm"

cd portfolio

# Add Quarkus extensions
quarkus ext add rest
quarkus ext add rest-jackson
quarkus ext add rest-client-jackson
quarkus ext add grpc
quarkus ext add smallrye-fault-tolerance
quarkus ext add smallrye-health
quarkus ext add web-dependency-locator
quarkus ext add container-image-docker
quarkus ext add renarde
quarkus ext add qute-web

cd ..

echo -e "${GREEN}Portfolio service created successfully!${NC}"

# ======================================
# Create Root POM
# ======================================
echo -e "\n${GREEN}Creating root pom.xml...${NC}"

cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>${GROUP_ID}</groupId>
    <artifactId>parent</artifactId>
    <version>${VERSION}</version>
    <packaging>pom</packaging>
    <name>Currency Exchange</name>

    <modules>
        <module>currency</module>
        <module>portfolio</module>
        <module>trades</module>
    </modules>
</project>
EOF

# Replace variables in pom.xml
sed -i.bak "s/\${GROUP_ID}/${GROUP_ID}/g" pom.xml
sed -i.bak "s/\${VERSION}/${VERSION}/g" pom.xml
rm pom.xml.bak

echo -e "${GREEN}Root pom.xml created successfully!${NC}"

# ======================================
# Summary
# ======================================
echo -e "\n${BLUE}======================================${NC}"
echo -e "${GREEN}Bootstrap complete!${NC}"
echo -e "${BLUE}======================================${NC}"
echo -e "\nCreated services:"
echo -e "  - ${GREEN}portfolio${NC} (port 8080) - Web UI and main application"
echo -e "  - ${GREEN}currency${NC}  (port 8082) - gRPC service for exchange rates"
echo -e "  - ${GREEN}trades${NC}    (port 8083) - REST service for trade execution"
echo -e "\nNext steps:"
echo -e "\nTo build all services:"
echo -e "  mvn clean package"
echo -e "\nTo run a service in dev mode:"
echo -e "  cd <service-name> && quarkus dev"
echo -e "${BLUE}======================================${NC}"
