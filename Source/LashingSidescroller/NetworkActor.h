// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include "GameFramework/Actor.h"
#include "Sockets.h"
#include "IPAddress.h"
#include "NetworkActor.generated.h"

UCLASS()
class LASHINGSIDESCROLLER_API ANetworkActor : public AActor
{
	GENERATED_BODY()
	
public:	
	// Sets default values for this actor's properties
	ANetworkActor();
	
	virtual ~ANetworkActor();

	// Called when the game starts or when spawned
	virtual void BeginPlay() override;
	
	// Called every frame
	virtual void Tick( float DeltaSeconds ) override;

	void TCPConnectionListener();
	void TCPSocketListener();

	UPROPERTY(BlueprintReadOnly, Transient, Category="Transformation")
	FRotator Rotator;

private:
	FSocket* Socket;
	FSocket* Connection;
	TSharedPtr<FInternetAddr> RemoteAddress;
};
