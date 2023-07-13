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
import com.example.ack.databinding.ItemPickingCabeceraBinding
import data.PickingCabecera

class PickingFilesAdapter(private val onClickListener: (PickingCabecera) -> Unit) : ListAdapter<PickingCabecera, PickingViewHolder>(PickingDiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_picking_cabecera, parent, false)
        return PickingViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: PickingViewHolder, position: Int) {
        val item = getItem(position)
        holder.render(item, onClickListener)
    }

}

class PickingViewHolder(view: View): RecyclerView.ViewHolder(view){

    private val binding = ItemPickingCabeceraBinding.bind(view)

    fun render(pickingCabecera: PickingCabecera, onClickListener: (PickingCabecera) -> Unit){

        //Escribe en los TextView
        "${pickingCabecera.TMPSer}/${pickingCabecera.TMPNro}".also { binding.TextTitle.text = it }

        binding.TextDescription.text = pickingCabecera.TMPCliNom

        //Listener al hacer clic
        itemView.setOnClickListener { onClickListener(pickingCabecera) }

        try {

            //Elige la opción en relación al estado
            when (pickingCabecera.TMPEst?.toInt()) {

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

                else -> {
                    //Estado COMPLETO
                    binding.TextEstado.text = "IMPORTADO"
                }

            }

        }catch(e : Exception){
        println(e.message)
    }

    }

}

private object PickingDiffUtilCallback : DiffUtil.ItemCallback<PickingCabecera>() {
    override fun areItemsTheSame(oldItem: PickingCabecera, newItem: PickingCabecera): Boolean =
        oldItem.TMPNro == newItem.TMPNro


    override fun areContentsTheSame(oldItem: PickingCabecera, newItem: PickingCabecera): Boolean =
        oldItem.TMPNro == newItem.TMPNro
}