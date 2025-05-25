# ContextNet-Encrypted-Messager

Este projeto visa proporcionar maior segurança na comunicação entre usuários da rede **ContextNet**, aplicando métodos de criptografia para proteger as mensagens transmitidas.

## Objetivo
Desenvolver uma solução de comunicação segura dentro da rede **ContextNet**, garantindo confidencialidade, integridade e autenticidade das mensagens trocadas entre os usuários.

## Tecnologias Utilizadas
- **ContextNet**: Rede utilizada para a comunicação entre dispositivos.
- **Java**: Linguagem de programação usada para implementar a lógica do sistema.
- **Criptografia**: Métodos de criptografia simétrica e/ou assimétrica para proteger as mensagens trocadas.

## Funcionalidades
- **Envio e Recebimento de Mensagens**: Comunicação entre dois nós da rede ContextNet (remetente e receptor).
- **Criptografia de Mensagens**: As mensagens são criptografadas antes de serem enviadas e descriptografadas após o recebimento.

## Como Executar
1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/contextnet-encrypt.git
   
2. Para gerar as chave públicas e privadas dos comunicadores, rode o comando abaixo no diretório do projeto. Esse comando gera dois arquivos, um para cada chave, que serão utilizadas posteriormente para realizar a comunicação entre os comunicadores;
   ```bash
   java -jar appKeys.jar generateKeys [nome-do-arquivo]

3. Para rodar esse comando é necessário já ter os arquivos de chave privada e pública já gerados para os dois comunicados do sistema. Tendo essas chaves em mãos, rode o seguinte comando para poder inicializa-los no sistema ContextNet. Nesse caso, o "sender" é o para o comunicador quie irá se registrar, e o "receiver" são os dados para qual vamos estabelecer a conexão.
   ```bash
   java -jar app.jar run [servidor] [porta] [uuid-sender] [privateKey-sender] [uuid-receiver] [receiver-publicKey]

Exemplo de comandos:
   ```bash
   java -jar target/app.jar sender bsi.cefet-rj.br 5500 cc2528b7-fecc-43dd-a1c6-188546f0ccbf 641f18ae-6c0c-45c2-972f-d37c309a9b72
   java -jar target/app.jar receiver bsi.cefet-rj.br 5500 641f18ae-6c0c-45c2-972f-d37c309a9b72

Exemplo de mensagens:
   ```bash
   <mid1,cc2528b7-fecc-43dd-a1c6-188546f0ccbf,askOne,641f18ae-6c0c-45c2-972f-d37c309a9b72,numeroDaSorte(N)>
   <mid1,cc2528b7-fecc-43dd-a1c6-188546f0ccbf,tell,641f18ae-6c0c-45c2-972f-d37c309a9b72,numeroDaSorte(3333)>


## Futuras Implementações
- Melhorias na interface de usuário.
- Implementação de novos algoritmos de criptografia.
- Auditoria de segurança automatizada.


## CEFET-RJ, Sistemas de Informação
