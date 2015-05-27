// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include "Components/ActorComponent.h"
#include "Sockets.h"
#include "IPAddress.h"
#include "NetworkRotatorComponent.generated.h"

UCLASS( ClassGroup=(Custom), meta=(BlueprintSpawnableComponent) )
class LASHINGSIDESCROLLER_API UNetworkRotatorComponent : public UActorComponent
{
	GENERATED_BODY()

public:	
	// Sets default values for this component's properties
	UNetworkRotatorComponent();

	virtual ~UNetworkRotatorComponent();

	// Called when the game starts
	virtual void BeginPlay() override;
	
	// Called every frame
	virtual void TickComponent( float DeltaTime, ELevelTick TickType, FActorComponentTickFunction* ThisTickFunction ) override;

	void TCPConnectionListener();
	void TCPSocketListener();

	UPROPERTY(BlueprintReadOnly, Transient, Category="Transformation")
	FRotator Rotator;

private:
	FSocket* Socket;
	FSocket* Connection;
	TSharedPtr<FInternetAddr> RemoteAddress;
};
