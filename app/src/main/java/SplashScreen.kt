package com.example.smartconverter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import android.view.animation.AccelerateDecelerateInterpolator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.Intent
import android.widget.ImageView

class SplashScreen : AppCompatActivity() {

    // splashDuration in milliseconds
    private val splashDuration = 1400L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val ivLogo = findViewById<ImageView>(R.id.iv_logo)

        // Start logo animation immediately
        startLogoAnimation(ivLogo)

        // Navigate to MainActivity after a short delay
        lifecycleScope.launch {
            delay(splashDuration)
            startMainAndFinish()
        }
    }

    private fun startLogoAnimation(ivLogo: ImageView) {
        // scaleX and scaleY: small pop in
        val scaleX = ObjectAnimator.ofFloat(ivLogo, "scaleX", 0.85f, 1.05f, 1f)
        val scaleY = ObjectAnimator.ofFloat(ivLogo, "scaleY", 0.85f, 1.05f, 1f)

        // slight vertical motion (subtle float)
        val translateY = ObjectAnimator.ofFloat(ivLogo, "translationY", 30f, -10f, 0f)

        // small rotation for a playful effect
        val rotation = ObjectAnimator.ofFloat(ivLogo, "rotation", -6f, 6f, 0f)

        // timing
        scaleX.duration = 900
        scaleY.duration = 900
        translateY.duration = 900
        rotation.duration = 900

        // interpolator for smooth feel
        val interpolator = AccelerateDecelerateInterpolator()
        scaleX.interpolator = interpolator
        scaleY.interpolator = interpolator
        translateY.interpolator = interpolator
        rotation.interpolator = interpolator

        // Run together
        val set = AnimatorSet()
        set.playTogether(scaleX, scaleY, translateY, rotation)
        set.start()
    }

    private fun startMainAndFinish() {
        val intent = Intent(this@SplashScreen, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
