# nvoip-java

Cliente Java simples para a API v2 da Nvoip, com foco nos fluxos principais de autenticação, ligações, OTP e WhatsApp.

## Requisitos

- Java 11+
- Maven 3.8+

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

## SDK web

Para o fluxo de popup com telefone e código, use em conjunto o repositório `nvoip-web-sdk`. Este repo cobre o consumo server-side da API.

## Documentação oficial

- https://nvoip.docs.apiary.io/
- https://www.nvoip.com.br/api
