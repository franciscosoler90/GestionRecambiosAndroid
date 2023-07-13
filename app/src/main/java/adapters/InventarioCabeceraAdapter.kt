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
import com.example.ack.databinding.ItemInventarioCabeceraBinding
import data.InventarioCabecera

class InventarioCabeceraAdapter(private val onClickListener: (InventarioCabecera) -> Unit) : ListAdapter<InventarioCabecera, InventarioCabeceraViewHolder>(ItemsDiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventarioCabeceraViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_inventario_cabecera, parent, false)
        return InventarioCabeceraViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: InventarioCabeceraViewHolder, position: Int) {
        val item = getItem(position)
        holder.render(item, onClickListener)
    }

}

class InventarioCabeceraViewHolder(view: View): RecyclerView.ViewHolder(view){

    private val binding = ItemInventarioCabeceraBinding.bind(view)

    fun render(inventarioCabecera: InventarioCabecera, onClickListener: (InventarioCabecera) -> Unit){

        //Escribe en el TextView
        "${inventarioCabecera.TMISer}/${inventarioCabecera.TMINro}".also { binding.TextTitle.text = it }

        //Listener cuando hace clic
        itemView.setOnClickListener { onClickListener(inventarioCabecera) }

        try {

            when (inventarioCabecera.TMIEst?.toInt()) {
                0 -> {
                    binding.TextEstado.text = "PENDIENTE"
                }
                1 -> {
                    binding.cardArticulo.setCardBackgroundColor(Color.parseColor("#B95722"))
                    binding.TextEstado.text = "EN CURSO"
                }
                2 -> {
                    binding.cardArticulo.setCardBackgroundColor(Color.parseColor("#B95722"))
                    binding.TextEstado.text = "PROCESADO"
                }
                3 -> {
                    binding.cardArticulo.setCardBackgroundColor(Color.parseColor("#008A10"))
                    binding.TextEstado.text = "IMPORTADO"
                }
            }

        }catch(e : Exception){
            println(e.message)
        }
    }
}

private object ItemsDiffUtilCallback : DiffUtil.ItemCallback<InventarioCabecera>() {
    override fun areItemsTheSame(oldItem: InventarioCabecera, newItem: InventarioCabecera): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: InventarioCabecera, newItem: InventarioCabecera): Boolean {
        return oldItem == newItem
    }

}