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
import data.PickingLineas
import java.util.*
import kotlin.collections.ArrayList

class PickingLineasAdapter(private val listaPicking: List<PickingLineas>, private val onClickListener: (PickingLineas) -> Unit) : ListAdapter<PickingLineas, PickingLineasViewHolder>(PickingLineasDiffUtilCallback) {

    private var listaFiltrada : ArrayList<PickingLineas> = ArrayList(listaPicking)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingLineasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_articulo, parent, false)
        return PickingLineasViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: PickingLineasViewHolder, position: Int) {

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
        if(listaPicking.isNotEmpty()){

            //El texto a buscar no está vacio
            if(newText.isNotEmpty()) {

                var cadena1: String
                var cadena2: String

                //Limpia la lista
                listaFiltrada.clear()

                //Bucle por cada item de la lista
                for (item in this.listaPicking) {

                    //Cadenas de texto en minusculas
                    cadena1 = item.TMPArtCod!!.lowercase(Locale.getDefault())
                    cadena2 = newText.lowercase(Locale.getDefault())

                    //Si contiene la cadena
                    if (cadena1.contains(cadena2)) {
                        listaFiltrada.add(item)
                    }
                }

            }else{
                //Pasa la lista nueva a la lista filtrada
                listaFiltrada = ArrayList(listaPicking)
            }

            //Envia la lista filtrada
            submitList(listaFiltrada.toList())

        }// La lista Original no está vacia
    }//fun filter

}

class PickingLineasViewHolder(view: View): RecyclerView.ViewHolder(view){

    private val binding = ItemArticuloBinding.bind(view)

    fun render(pickingLinea: PickingLineas, onClickListener: (PickingLineas) -> Unit){

        val descripcion = "Código: "
        val estado = "Ubicación: "

        //Escribe en los TextView
        binding.TextTitle.text = pickingLinea.TMPArtDes
        binding.labelTextDescription.text = descripcion
        binding.TextDescription.text = pickingLinea.TMPArtCod
        binding.labelTextDescription2.text = estado
        binding.TextDescription2.text = pickingLinea.TMPArtUbi

        //Listener al hacer clic
        itemView.setOnClickListener { onClickListener(pickingLinea) }

        try{

            //Elige la opción en relación al estado
            when (pickingLinea.TMPLinEst?.toInt()) {

                0 -> {
                    //Estado PENDIENTE
                    binding.TextEstado.text = "PENDIENTE"
                }

                1 -> {
                    //Estado EN CURSO
                    binding.TextEstado.text = "EN CURSO"

                    //Color Naranja
                    binding.cardArticulo.setCardBackgroundColor(Color.parseColor("#B95722"))
                }

                2 -> {
                    //Estado COMPLETO
                    binding.TextEstado.text = "COMPLETADO"

                    //Color Verde
                    binding.cardArticulo.setCardBackgroundColor(Color.parseColor("#008A10"))
                }

            }

        }catch(e : Exception){
            println(e)
        }
    }
}

private object PickingLineasDiffUtilCallback : DiffUtil.ItemCallback<PickingLineas>() {
    override fun areItemsTheSame(oldItem: PickingLineas, newItem: PickingLineas): Boolean {
        return oldItem.TMPArtLin == newItem.TMPArtLin
    }
    override fun areContentsTheSame(oldItem: PickingLineas, newItem: PickingLineas): Boolean {
        return oldItem.TMPArtLin == newItem.TMPArtLin
    }
}