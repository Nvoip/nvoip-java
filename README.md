# nvoip-java

[![CI](https://github.com/Nvoip/nvoip-java/actions/workflows/ci.yml/badge.svg)](https://github.com/Nvoip/nvoip-java/actions/workflows/ci.yml) [![Maven Central](https://img.shields.io/maven-central/v/br.com.nvoip/nvoip-java?style=flat-square)](https://central.sonatype.com/artifact/br.com.nvoip/nvoip-java) [![Nvoip](https://img.shields.io/badge/Nvoip-site-00A3E0?style=flat-square)](https://www.nvoip.com.br/) [![API v2](https://img.shields.io/badge/API-v2-1F6FEB?style=flat-square)](https://www.nvoip.com.br/api/) [![Docs](https://img.shields.io/badge/docs-Apiary-6A737D?style=flat-square)](https://nvoip.docs.apiary.io/) [![Postman](https://img.shields.io/badge/Postman-workspace-FF6C37?style=flat-square)](https://nvoip-api.postman.co/workspace/e671d01f-168a-4c38-8d0e-c217229dd61a/team-quickstart) [![Stack](https://img.shields.io/badge/stack-Java-ED8B00?style=flat-square)](https://github.com/Nvoip/nvoip-api-examples) [![License: GPL-3.0](https://img.shields.io/badge/license-GPL--3.0-blue?style=flat-square)](LICENSE)

SDK e exemplos oficiais da [Nvoip](https://www.nvoip.com.br/) para integrar a API v2 com OAuth, chamadas, OTP, WhatsApp, SMS e saldo em Java.

## Requisitos

- Java 11+
- Maven 3.8+

## Instalacao

```xml
<dependency>
  <groupId>br.com.nvoip</groupId>
  <artifactId>nvoip-java</artifactId>
  <version>0.1.0</version>
</dependency>
```

## Configuração

```bash
cp .env.example .env
```

Ou exporte:

```bash
export NVOIP_NUMBERSIP="seu_numbersip"
export NVOIP_USER_TOKEN="seu_user_token"
export NVOIP_OAUTH_CLIENT_ID="seu_client_id"
export NVOIP_OAUTH_CLIENT_SECRET="seu_client_secret"
export NVOIP_CALLER="1049"
export NVOIP_TARGET_NUMBER="11999999999"
```

## Build

```bash
mvn -q -DskipTests package
```

## Exemplos

```bash
java -cp target/classes br.com.nvoip.examples.Main auth-token
java -cp target/classes br.com.nvoip.examples.Main balance
java -cp target/classes br.com.nvoip.examples.Main send-sms
java -cp target/classes br.com.nvoip.examples.Main create-call
java -cp target/classes br.com.nvoip.examples.Main send-otp
java -cp target/classes br.com.nvoip.examples.Main check-otp
java -cp target/classes br.com.nvoip.examples.Main wa-list
java -cp target/classes br.com.nvoip.examples.Main wa-send
```

### Destinatário WhatsApp

O exemplo mantém `NVOIP_WA_DESTINATION` para telefone. Para o contrato tipado,
use `NVOIP_WA_RECIPIENT_TYPE=phone|bsuid|parent_bsuid` e
`NVOIP_WA_RECIPIENT_VALUE`, sem `destination`. BSUID é opaco; não use
`@username` nem o coloque em campo de telefone. Exemplos mascarados:
`US.MASKED_BSUID_001` e `PARENT.MASKED_BSUID_001`.

## SDK web

Para o fluxo de popup com telefone e código, use em conjunto o repositório `nvoip-web-sdk`. Este repo cobre o consumo server-side da API.

## Links oficiais

- [Site da Nvoip](https://www.nvoip.com.br/)
- [Documentação da API](https://nvoip.docs.apiary.io/)
- [Página da API](https://www.nvoip.com.br/api/)
- [Workspace Postman](https://nvoip-api.postman.co/workspace/e671d01f-168a-4c38-8d0e-c217229dd61a/team-quickstart)
- [Hub de exemplos](https://github.com/Nvoip/nvoip-api-examples)
