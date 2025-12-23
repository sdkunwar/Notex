# NoteX Keystore

This directory contains the release signing keystore for NoteX.

## Generating the Keystore

To generate the release keystore, run:

```bash
keytool -genkey -v \
  -keystore notex-release.jks \
  -alias notex \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass notexapp123 \
  -keypass notexapp123 \
  -dname "CN=NoteX, OU=Development, O=NoteX, L=Unknown, ST=Unknown, C=US"
```

## Configuration

The `keystore.properties` file contains the keystore configuration:
- `storeFile`: Path to the keystore file
- `storePassword`: Keystore password
- `keyAlias`: Key alias
- `keyPassword`: Key password

## CI/CD Setup

For GitHub Actions, set the following secrets:
- `KEYSTORE_BASE64`: Base64 encoded keystore file
- `KEYSTORE_PASSWORD`: Keystore password
- `KEY_ALIAS`: Key alias
- `KEY_PASSWORD`: Key password

To encode the keystore:
```bash
base64 -i notex-release.jks -o keystore_base64.txt
```

## Security Note

For open source projects, it's common to include a public release keystore.
This allows anyone to build and sign the same APK, ensuring reproducible builds.
The security of the signature relies on the private key, not the password secrecy.
