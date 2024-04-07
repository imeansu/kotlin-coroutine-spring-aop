package com.example.coroutineaop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CoroutineAopApplication

fun main(args: Array<String>) {
	runApplication<CoroutineAopApplication>(*args)
}
