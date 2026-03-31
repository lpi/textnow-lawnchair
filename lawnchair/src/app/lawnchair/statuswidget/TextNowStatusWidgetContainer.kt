package app.lawnchair.statuswidget

import android.content.Context
import android.text.format.Formatter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import app.lawnchair.LawnchairLauncher
import app.lawnchair.launcher
import app.lawnchair.launcherNullable
import com.android.launcher3.R
import com.android.launcher3.statuswidget.StatusWidgetRepository

class TextNowStatusWidgetContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    init {
        val inflater = LayoutInflater.from(context)
        val content = inflater.inflate(R.layout.textnow_status_widget_content, this, false)

        val dp = LawnchairLauncher.instance?.launcherNullable?.deviceProfile
        val leftPadding = dp?.widgetPadding?.left ?: 16
        content.setPadding(leftPadding, content.paddingTop, content.paddingRight, content.paddingBottom)

        bindData(content)

        content.setOnClickListener {
            TextNowOffersSheet.show(context.launcher)
        }

        addView(content)
    }

    private fun bindData(view: View) {
        val data = StatusWidgetRepository.getStatusData(view.context)

        val progressBar = view.findViewById<ProgressBar>(R.id.data_progress) ?: return
        val dataLabel = view.findViewById<TextView>(R.id.data_label) ?: return
        val pointsLabel = view.findViewById<TextView>(R.id.points_label) ?: return
        val offersLabel = view.findViewById<TextView>(R.id.offers_label) ?: return

        val usedPercent = if (data.dataTotalBytes > 0) {
            ((data.dataUsedBytes * 100) / data.dataTotalBytes).toInt()
        } else {
            0
        }
        val remainingBytes = data.dataTotalBytes - data.dataUsedBytes
        progressBar.progress = 100 - usedPercent

        val remainingFormatted = Formatter.formatShortFileSize(context, remainingBytes)
        val totalFormatted = Formatter.formatShortFileSize(context, data.dataTotalBytes)
        dataLabel.text = context.getString(
            R.string.textnow_status_data_remaining,
            remainingFormatted,
            totalFormatted,
        )

        pointsLabel.text = context.getString(R.string.textnow_status_points_label, data.loyaltyPoints)
        offersLabel.text = context.getString(R.string.textnow_status_offers_label, data.offersAvailable)
    }
}
