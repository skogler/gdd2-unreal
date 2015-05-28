// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include "GameFramework/PlayerController.h"
#include "NetworkPlayerController.generated.h"


UCLASS()
class AST3ROIDS_API ANetworkPlayerController : public APlayerController
{
	GENERATED_BODY()

public:
	// Sets default values for this actor's properties
	ANetworkPlayerController(const FObjectInitializer& ObjectInitializer);

	virtual ~ANetworkPlayerController();

	// Called when the game starts or when spawned
	virtual void BeginPlay() override;

	// Called every frame
	virtual void Tick(float DeltaSeconds) override;

	void TCPConnectionListener();
	void TCPSocketListener();

	//UFUNCTION(BlueprintCallable, Category = "Pawn")
	virtual FRotator GetControlRotation() const override;

	UPROPERTY(BlueprintReadOnly, Transient, Category = "Transformation")
		FRotator InputRotator;

private:
	FSocket* Socket;
	FSocket* Connection;
	TSharedPtr<FInternetAddr> RemoteAddress;
	
	
};
