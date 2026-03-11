package com.assessment.miniwallet;

import org.springframework.boot.SpringApplication;

public class TestMiniwalletApplication {

	public static void main(String[] args) {
		SpringApplication.from(MiniWalletApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}