// Agent bob in project multiAgentSystem

/* Initial beliefs and rules */
iotGateway("192.168.0.108",5500).
myUUID("cc2528b7-fecc-43dd-a1c6-188546f0ccbf").
app("641f18ae-6c0c-45c2-972f-d37c309a9b72").

/* Initial goals */
!start.

/* Plans */
+!start: iotGateway(Server,Port) & myUUID(ID) <- 
  .hermes.configureContextNetConnection("skyNET", Server, Port, ID);
  .hermes.connect("skyNET").

/* initial plan */
!start.
//!forcaTell.
//!forcaAskOne.
//!forcaAchieve.
//!forcaTellHow.

+!forcaTell <-  
	?app(K);
	.wait(5000);
	.hermes.sendOut(K, tell, crenca(valor));
	!forcaTell.

+!forcaAskOne <-  
	?app(K);
	.wait(5000);
	.hermes.sendOut(K, askOne, crenca(V));
	!forcaAskOne.
	
+!forcaAchieve <- 
	?app(K);
	.wait(5000);
	.hermes.sendOut(K, achieve, plano(V));
	!forcaAchieve.

+!forcaTellHow <-
	?app(K);
	.wait(5000);
	.hermes.sendOut(K, tellHow, "+!ensinamento(J)[source(Origem)] <- .print(\"Recebi o plano: \", J, \" \", Origem)");
	!forcaTellHow.

+!plano(M)[source(Origem)] <- .print("Executei o plano: ", M, " ", Origem).

+numeroDaSorte(S)[source(Origem)] <- .print("Recebi o parametro: ", S, " ", Origem).

//+!ensinamento(J)[source(Origem)] <- .print("Recebi o plano: ",J," ",Origem).