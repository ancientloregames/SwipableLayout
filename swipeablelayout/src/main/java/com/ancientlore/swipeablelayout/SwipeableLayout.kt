package com.ancientlore.swipeablelayout

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.annotation.Nullable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class SwipeableLayout: ViewGroup {

	private lateinit var centerView: View
	private var leftView: View? = null
		set(value) { if (field == null) field = value }
	private var rightView: View? = null
		set(value) { if (field == null) field = value }

	constructor(context: Context): super(context) { init(context) }

	constructor(context: Context, @Nullable attrs: AttributeSet?): super(context, attrs) { init(context) }

	constructor(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) { init(context) }

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes) { init(context) }

	private fun init(context: Context) {
	}

	override fun onFinishInflate() {
		super.onFinishInflate()

		initViews()
	}

	private fun initViews() {
		for (i in 0..childCount) {
			val child = getChildAt(i)
			val layoutParams = child.layoutParams as LayoutParams
			when (layoutParams.gravity) {
				LayoutParams.LEFT -> leftView = child
				LayoutParams.CENTER -> centerView = child
				LayoutParams.RIGHT -> rightView = child
			}
		}

		if (centerView == null)
			throw RuntimeException("Center view in layout is mandatory!")
	}

	override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
		for (i in 0..childCount) {
			val child = getChildAt(i)
			if (child.visibility != View.GONE) {
				val lp = child.layoutParams as LayoutParams

				val width = child.measuredWidth
				val height = child.measuredHeight

				val orientation = lp.gravity

				val childTop = paddingTop
				val childLeft = when (orientation) {
					LayoutParams.LEFT -> centerView.left - width
					LayoutParams.RIGHT -> centerView.right
					LayoutParams.CENTER -> child.left
					else -> child.left
				}

				child.layout(childLeft, childTop, childLeft + width, childTop + height)
			}
		}
	}

	class LayoutParams : ViewGroup.LayoutParams {

		companion object {
			const val LEFT = 0
			const val CENTER = 1
			const val RIGHT = 2
		}

		var gravity: Int = CENTER
			private set

		constructor(source: ViewGroup.LayoutParams): super(source)

		constructor(width: Int, height: Int): super(width, height)

		constructor(c: Context, attrs: AttributeSet): super(c, attrs) {

			val attrs = c.obtainStyledAttributes(attrs, R.styleable.SwipeableLayout)

			for (i in 0..attrs.indexCount) {
				val attr = attrs.getIndex(i)
				when (attr) {
					R.styleable.SwipeableLayout_gravity -> gravity = attrs.getInt(attr, CENTER)
				}
			}
		}

	}
}