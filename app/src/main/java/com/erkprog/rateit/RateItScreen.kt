package com.erkprog.rateit

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Vibrator
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.materialIcon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.res.ResourcesCompat
import com.erkprog.rateit.components.*
import com.erkprog.rateit.model.Rating
import com.erkprog.rateit.util.FaceShakeAnimation
import com.erkprog.rateit.util.shakeFace
import java.nio.file.Files.size
import kotlin.math.roundToInt

@Preview
@ExperimentalAnimationApi
@Composable
fun RateItScreen(modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenHeight = configuration.screenHeightDp
    val screenWidth = configuration.screenWidthDp.dp
    Box(modifier = modifier) {

        var progress by remember { mutableStateOf(0.5f) }

        val rating: State<Rating> = remember {
            derivedStateOf {
                when (progress) {
                    in 0f..0.35f -> Rating.HIDEOUS
                    in 0.35f..0.7f -> Rating.OK
                    in 0.40f..0.7f -> Rating.AVERAGE
                    in 0.100f..0.8f -> Rating.GOOD
                    else -> Rating.EXCELLENT
                }
            }
        }


        var sliderStartWindowVector by remember { mutableStateOf(Offset.Zero) }
        var sliderEndWindowVector by remember { mutableStateOf(Offset.Zero) }

        val interactionSource = remember { MutableInteractionSource() }
        var sliderPositioned by remember { mutableStateOf(false) }

        val dragged by interactionSource.collectIsDraggedAsState()
        val pressed by interactionSource.collectIsPressedAsState()

        val shake by remember {
            derivedStateOf { progress < FaceShakeAnimation.shakeThreshold }
        }

        val width = LocalConfiguration.current.screenWidthDp
        val shakeValue = (width * 0.01f).roundToInt().coerceAtLeast(2)
        val shakeOffset =
            remember { Animatable(IntOffset.Zero, IntOffset.VectorConverter) }

        val haptic = LocalHapticFeedback.current.performHapticFeedback(HapticFeedbackType.LongPress)

        BgColor(
            progress = progress, modifier = Modifier
                .fillMaxSize()
                .zIndex(0f)


        )

        Column(
            modifier = Modifier
                .padding(vertical = 35.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RatingTitle(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .weight(0.4f),
                rating = rating.value


            )
            AnimatedFace(
                modifier = Modifier
                    .weight(0.55f)
                    .fillMaxWidth()
                    .offset { shakeOffset.value },
                sliderPositioned = sliderPositioned,
                progress = progress,
                sliderStartWindowVector = sliderStartWindowVector,
                sliderEndWindowVector = sliderEndWindowVector,
                focusedOnSlider = dragged || pressed

            )

            /*customSlider*/
            CustomSlider(
                modifier = Modifier
                    .weight(0.15f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .onGloballyPositioned {
                        sliderStartWindowVector = it.positionInWindow()
                        sliderEndWindowVector =
                            it.positionInWindow() + Offset(it.size.width.toFloat(), 0f)
                        sliderPositioned = true
                    },
                value = progress,
                onValueChange = {
                    progress = it
                },
                interactionSource = interactionSource
            )

            Row(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp, end = 20.dp, start = 20.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier
                        .align(CenterVertically)
                        .padding(end = 5.dp),
                    painter = painterResource(R.drawable.ic_baseline_message_24),
                    contentDescription = null
                )


                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = "Add a comment",
                    style = TextStyle(
                        color = Color.Black.copy(),
                        fontSize = 19.sp
                    ),
                    textAlign = TextAlign.Start
                )
                Spacer(Modifier.padding(start = 150.dp))
            }

            Row(
                modifier = Modifier
                    .padding(end = 10.dp, start = 10.dp, top = 10.dp),
            ) {
                Button(
                    onClick = {
                        Toast.makeText(context, "Fred CLicked Me", Toast.LENGTH_LONG).show()
                    },
                    /*modifier = Modifier.padding(),*/
                    modifier = Modifier.size(width = 319.dp, height = 58.dp),
                    enabled = true,
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color(color = 0xFF101D70),
                        contentColor = Color.White
                    ),
                    // painterResource(id = R.drawable.ic_baseline_message_24),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = SolidColor(Color(color = 0xFF101D70))
                    ),
                   // shape = MaterialTheme.shapes.medium
                )
                {
                    /*val vector = ImageVector.vectorResource(id = R.drawable.ic_launcher_foreground)
                    val painter = rememberVectorPainter(image = vector)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        with(painter) {
                            draw(painter.intrinsicSize)
                        }
                    }*/


                    Text(
                        modifier = Modifier.padding(start = 10.dp, end = 30.dp),
                        text = "Confirm",
                        style = TextStyle(
                            color = Color.White.copy(),
                            fontSize = 18.sp

                        ),
                        textAlign = TextAlign.Start
                    )
                   // Spacer(Modifier.size(ButtonDefaults.IconSpacing))

                    /*  Icon(
                        modifier = Modifier
                            .size(width = 60.dp, height = 60.dp)

                            . padding(start = 200.dp),
                        painter = painterResource(R.drawable.ic_baseline_message_24),
                        tint = Color(color = 0xFFE91E63),
                        contentDescription = null
                    )*/





                    Icon(
                        painter = painterResource(id = R.drawable.ic_circle),
                        contentDescription = "Favorite",
                        modifier = Modifier
                            .fillMaxSize(1.8f)
                            // .size(width = 60.dp, height = 60.dp),
                            .padding(start = 140.dp, end = 0.dp),
                        )
                   Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_arrow_back_24),
                        contentDescription = "Favorite",
                        modifier = Modifier
                            .size(width = 60.dp, height = 60.dp),

                        )



                }


            }
        }



        LaunchedEffect(shake) {
            if (shake) {
                shakeOffset.shakeFace(shakeValue)
            } else {
                shakeOffset.animateTo(IntOffset.Zero)
            }
        }
    }
}

