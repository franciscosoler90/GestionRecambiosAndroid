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
import com.example.ack.databinding.ItemArticuloBinding
import data.InventarioLineas
import java.util.*

class InventarioAdapter(private val listaInventario: List<InventarioLineas>, private val onClickListener: (InventarioLineas) -> Unit) : ListAdapter<InventarioLineas, InventarioViewHolder>(InventarioDiffUtilCallback) {

    private var listaFiltrada : ArrayList<InventarioLineas> = ArrayList(listaInventario)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventarioViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_articulo, parent, false)
        return InventarioViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: InventarioViewHolder, position: Int) {

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
        if(listaInventario.isNotEmpty()){

            //El texto a buscar no está vacio
            if(newText.isNotEmpty()) {

                var cadena1: String
                var cadena2: String

                //Limpia la lista
                listaFiltrada.clear()

                //Bucle por cada item de la lista
                for (item in this.listaInventario) {

                    //Cadenas de texto en minusculas
                    cadena1 = item.TMIArtCod!!.lowercase(Locale.getDefault())
                    cadena2 = newText.lowercase(Locale.getDefault())

                    //Si contiene la cadena
                    if (cadena1.contains(cadena2)) {
                        listaFiltrada.add(item)
                    }
                }

            }else{
                //Pasa la lista nueva a la lista filtrada
                listaFiltrada = ArrayList(listaInventario)
            }

            //Envia la lista filtrada
            submitList(listaFiltrada.toList())

        }// La lista Original no está vacia
    }//fun filter

}


class InventarioViewHolder(view: View): RecyclerView.ViewHolder(view){

    private val binding = ItemArticuloBinding.bind(view)

    fun render(inventarioLineas: InventarioLineas, onClickListener: (InventarioLineas) -> Unit){

        //Escribe en los TextView
        binding.TextTitle.text = inventarioLineas.TMIArtDes
        binding.TextDescription.text = inventarioLineas.TMIArtCod
        binding.TextDescription2.text = inventarioLineas.TMIArtUbi


        //Listener al hacer clic
        itemView.setOnClickListener { onClickListener(inventarioLineas) }

        try {

            when (inventarioLineas.TMILinEst?.toInt()) {
                0 -> binding.TextEstado.text = "PENDIENTE"
                1 -> {
                    binding.cardArticulo.setCardBackgroundColor(Color.parseColor("#008A10"))
                    binding.TextEstado.text = "CONTADO"

                    //Unidades contadas
                    binding.textUnidades.text = "Unidades:"

                    val unidadesConDecimales = inventarioLineas.TMIUniCon?.toDouble()
                    val unidadesFormateadas = String.format("%.2f", unidadesConDecimales)
                    binding.textUnidades2.text = unidadesFormateadas
                }
            }

        }catch(e : Exception){
            println(e.message)
        }


    }
}

private object InventarioDiffUtilCallback : DiffUtil.ItemCallback<InventarioLineas>() {
    override fun areItemsTheSame(oldItem: InventarioLineas, newItem: InventarioLineas): Boolean {
        return oldItem.TMIArtCod == newItem.TMIArtCod
    }
    override fun areContentsTheSame(oldItem: InventarioLineas, newItem: InventarioLineas): Boolean {
        return oldItem.TMIArtCod == newItem.TMIArtCod
    }
}