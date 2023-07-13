/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ack.R
import com.example.ack.databinding.ItemMercanciaBinding
import data.MercanciaLineas
import java.util.*

class CabecerasLineasAdapter(private val listaCabecera: List<MercanciaLineas>, private val onClickListener: (MercanciaLineas) -> Unit) : ListAdapter<MercanciaLineas, CabeceraLineaViewHolder>(CabeceraLineaDiffUtilCallback) {

    private var listaFiltrada : ArrayList<MercanciaLineas> = ArrayList(listaCabecera)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CabeceraLineaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_mercancia, parent, false)
        return CabeceraLineaViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: CabeceraLineaViewHolder, position: Int) {

        //Desactiva el recycling
        //holder.setIsRecyclable(false)

        val item = getItem(position)
        holder.render(item, onClickListener)
    }

    override fun getItemCount(): Int {
        //Devuelve el tamaño de la lista filtrada
        return listaFiltrada.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun filter(newText : String){

        // La lista Original no está vacia
        if(listaCabecera.isNotEmpty()){

            //El texto a buscar no está vacio
            if(newText.isNotEmpty()) {

                var cadena1: String
                var cadena2: String

                //Limpia la lista
                listaFiltrada.clear()

                //Bucle por cada item de la lista
                for (item in this.listaCabecera) {

                    //Cadenas de texto en minusculas
                    cadena1 = item.TMCAriArtC!!.lowercase(Locale.getDefault())
                    cadena2 = newText.lowercase(Locale.getDefault())

                    //Si contiene la cadena
                    if (cadena1.contains(cadena2)) {
                        listaFiltrada.add(item)
                    }
                }

            }else{
                //Pasa la lista nueva a la lista filtrada
                listaFiltrada = ArrayList(listaCabecera)
            }

            //Envia la lista filtrada
            submitList(listaFiltrada.toList())

        }// La lista Original no está vacia
    }//fun filter
}

class CabeceraLineaViewHolder(view: View): RecyclerView.ViewHolder(view){

    private val binding = ItemMercanciaBinding.bind(view)

    fun render(linea: MercanciaLineas, onClickListener: (MercanciaLineas) -> Unit){

        //Escribe en los TextView
        binding.TextTitle.text = linea.TMCAriArtD?.trim() ?: linea.TMCAriArtD

        binding.TextDescription1.text = linea.TMCAriUni
        binding.TextDescription2.text = linea.TMCUniCont
        binding.TextDescription3.text = linea.TMCAriAlmC
        binding.TextDescription4.text = linea.TMCAriUbi
        binding.TextDescription5.text = linea.TMCAriArtC

        try {

            val num1: Float? = linea.TMCAriUni?.toFloat()

            if (num1 != null) {
                if (num1.rem(1) == 0F) {
                    val entero = num1.toInt()
                    binding.TextDescription1.text = entero.toString()
                } else {
                    val formattedNum1 = "%.2f".format(num1)
                    binding.TextDescription1.text = formattedNum1
                }
            }

            val num2: Float? = linea.TMCUniCont?.toFloat()

            if (num2 != null) {
                if (num2.rem(1) == 0F) {
                    val entero = num2.toInt()
                    binding.TextDescription2.text = entero.toString()
                } else {
                    val formattedNum2 = "%.2f".format(num2)
                    binding.TextDescription2.text = formattedNum2
                }
            }

        }catch(e : Exception){
            binding.TextDescription1.text = linea.TMCAriUni
            binding.TextDescription2.text = linea.TMCUniCont
        }

        //Listener al hacer clic
        itemView.setOnClickListener { onClickListener(linea) }

        try {

            when (linea.TMCEstLin?.toInt()) {

                0 -> {}
                1 -> {

                    if(linea.TMCAriUni == linea.TMCUniCont){
                        binding.cardArticulo.setCardBackgroundColor(Color.parseColor("#008A10"))
                    }else{
                        binding.cardArticulo.setCardBackgroundColor(Color.parseColor("#B95722"))
                    }

                }

            }

        }catch(e : Exception){
            println(e.message)
        }

    }
}

private object CabeceraLineaDiffUtilCallback : DiffUtil.ItemCallback<MercanciaLineas>() {
    override fun areItemsTheSame(oldItem: MercanciaLineas, newItem: MercanciaLineas): Boolean {
        return oldItem.TMCAriArtC == newItem.TMCAriArtC
    }
    override fun areContentsTheSame(oldItem: MercanciaLineas, newItem: MercanciaLineas): Boolean {
        return oldItem.TMCAriArtC == newItem.TMCAriArtC
    }
}