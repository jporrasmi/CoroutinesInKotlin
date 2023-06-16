package com.example.coroutinesinkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

//Para los Flow
//https://youtu.be/vQ0w4zAe68A

class MainActivity : AppCompatActivity() {
    val retrofit = RetrofitHelper.getInstance()

    //Dispachers indica donde va a correr el hilo.
    //1. Mian = hilo principal. Pinta la UI, si se hacen mas cosas de la cuenta se bloquea la pantalla
    //al usuario. Nunca se debe hacer nada que no sea del hilo principal.
    //2. IO =. Para llamar procesos que tardan pero no tanto como, Retrofit
    //3.  Default = Procesar informacion. Si es algo que necesita
    //mucho CPU, algo pesado.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Corrutina #1
        /*
        lifecycleScope.launch(Dispatchers.IO) {

           //La consulta se hace el el dispacher.io
           //val response:Response<SuperHeroDataResponse> = retrofit.getSuperheroes("a")

           // Pero el toast lo ejecuta en el hilo principal
           //Opcion 1

            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    Toast.makeText(this@MainActivity, "FUNCIONA", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
           */

            //Opcion 2
            /*
            if(response.isSuccessful){
                //Con runOnUiThread esa parte correo en el hilo principal
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "FUNCIONA", Toast.LENGTH_SHORT).show()
                }
            }

          }
        */

        //Corrutina Opcion #2. La consulta se hace el el dispacher.io
        lifecycleScope.launch(Dispatchers.IO) {

             val response:Response<SuperHeroDataResponse> = retrofit.getSuperheroes("a")

            //Si se envía como parámetro Dispatchers.Main lo ejecuta en el hilo principal y si muestra el mensaje.
            //Opcion 1
           // withContext(Dispatchers.Main){
           //     Toast.makeText(this@MainActivity, "Ejecuta la 2da", Toast.LENGTH_SHORT).show()

            //Estas siguientes tres lineas dan error porque el toast solo se puede ejecutar en el hilo principal.
            //  if(response.isSuccessful){
              //       Toast.makeText(this@MainActivity, "FUNCIONA", Toast.LENGTH_SHORT).show()
              //   }
            //}


            //Opcion 3: Se le puede decir que ejecuta lo del toast en el UIThread.
           /*
            if(response.isSuccessful){
                //Con runOnUiThread esa parte correo en el hilo principal
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "FUNCIONA", Toast.LENGTH_SHORT).show()
                }
            }
            */


            //Opcion #4
            runOnUiThread {
                Toast.makeText(this@MainActivity, "Va a invocar la coroutina", Toast.LENGTH_SHORT).show()
            }
            waitForCoroutines()

            runOnUiThread {
                Toast.makeText(this@MainActivity, "Ya a invocó a la coroutina", Toast.LENGTH_SHORT).show()
            }

      //  }
        }


        suspend fun suscribete() {
            //Las funciones suspendidas solo puede ser llamadas desde otra subrutina
            // o desde otra funcion suspendida.
        }

    }//on Create

    //Opcion #1 de waitForCoroutines
    /*
    private suspend fun waitForCoroutines() {
        lifecycleScope.launch(Dispatchers.IO) {
            val deferred1: Deferred<Response<SuperHeroDataResponse>> =
                async { retrofit.getSuperheroes("ironman") }
            //la clase deferred encapsula la respuesta, ha lanzado la petición
            //pero aún no se sabe cuánto tardará.

            val response = deferred1.await()

            runOnUiThread {
                Toast.makeText(this@MainActivity, "Response tiene" + response.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }*/
    //Opcion #2 de waitForCoroutines
    /*
    private suspend fun waitForCoroutines() {
        lifecycleScope.launch(Dispatchers.IO) {
            val deferred1: Deferred<Response<SuperHeroDataResponse>> = async { retrofit.getSuperheroes("a") }
            val deferred2: Deferred<Response<SuperHeroDataResponse>> = async { retrofit.getSuperheroes("b") }
            val deferred3: Deferred<Response<SuperHeroDataResponse>> = async { retrofit.getSuperheroes("c") }
            val deferred4: Deferred<Response<SuperHeroDataResponse>> = async { retrofit.getSuperheroes("d") }
            //la clase deferred encapsula la respuesta, ha lanzado la petición
            //pero aún no se sabe cuánto tardará.

            //Con el siguiente await la ejecución se va a detener hasta que la respuesta regrese del dispacher
            val response1: Response<SuperHeroDataResponse> = deferred1.await()
            val response2 : Response<SuperHeroDataResponse> = deferred2.await()
            val response3 : Response<SuperHeroDataResponse> = deferred3.await()
            val response4 : Response<SuperHeroDataResponse> = deferred4.await()

            runOnUiThread {
                Toast.makeText(this@MainActivity, "Response tiene" + response1.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
    */
    //Opcion #3 de waitForCoroutines
    private suspend fun waitForCoroutines() {
        lifecycleScope.launch(Dispatchers.IO) {
              val deferreds: List<Deferred<Response<SuperHeroDataResponse>>> = listOf(
                  async { retrofit.getSuperheroes("a") },
                  async { retrofit.getSuperheroes("b") },
                  async { retrofit.getSuperheroes("c") },
                  async { retrofit.getSuperheroes("d") },
                  async { retrofit.getSuperheroes("e") }
              )
            val response: List<Response<SuperHeroDataResponse>> = deferreds.awaitAll()

            runOnUiThread {
                Toast.makeText(this@MainActivity, "Espero por todos." , Toast.LENGTH_SHORT).show()
            }
        }
    }

    //ref: https://www.youtube.com/watch?v=vQ0w4zAe68A

} //Main



