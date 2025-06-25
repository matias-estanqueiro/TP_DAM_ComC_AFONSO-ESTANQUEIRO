package com.example.gymapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.gymapp.R
import com.example.gymapp.data.dt.DtPendingPayment
import com.example.gymapp.ui.payments.RegisterPaymentActivity

/**
 * Custom adapter to display a list of members with outstanding payments in a ListView.
 *
 * @param context The context of the application.
 * @param pendingPayments A list of [DtPendingPayment] objects to display.
 */
class PendingPaymentsListAdapter(
    context: Context,
    private val pendingPayments: MutableList<DtPendingPayment>)
: ArrayAdapter<DtPendingPayment>(context, 0, pendingPayments) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(
                R.layout.item_pending_payments_list,
                parent,
                false
            )
        }

        val pendingData = getItem(position)

        if (pendingData != null) {
            val tvMemberName: TextView = listItemView!!.findViewById(R.id.tvMemberName)
            val tvMemberDni: TextView = listItemView.findViewById(R.id.tvMemberDni)
            val tvMembershipInfo: TextView = listItemView.findViewById(R.id.tvMembershipInfo)
            val tvPendingMonths: TextView = listItemView.findViewById(R.id.tvPendingMonths)

            val btnRegisterPayment: ImageView = listItemView.findViewById(R.id.btnRegisterPayment)

            tvMemberName.text = context.getString(
                R.string.FORMAT_full_name, pendingData.client.surname, pendingData.client.name
            )
            tvMemberDni.text = pendingData.client.dni
            tvMembershipInfo.text = pendingData.client.planName
            tvPendingMonths.text =
                context.getString(R.string.FORMAT_pending_payment, pendingData.pendingMonthYear)

            btnRegisterPayment.setOnClickListener {
                val intent = Intent(context, RegisterPaymentActivity::class.java)
                context.startActivity(intent)
            }
        }

        return listItemView!!
    }

    fun updateData(newPendingPayments: List<DtPendingPayment>) {
        pendingPayments.clear()
        pendingPayments.addAll(newPendingPayments)
        notifyDataSetChanged()
    }
}