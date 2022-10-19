package com.javadEsl.pixel.ui.gallery

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.javadEsl.pixel.NetworkHelper
import com.javadEsl.pixel.data.PixelRepository
import com.javadEsl.pixel.data.topics.TopicsModelItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val pixelRepository: PixelRepository,
    private val networkHelper: NetworkHelper,
    private val pref: SharedPreferences,
) : ViewModel() {

    val allPhotos = pixelRepository.getAllPhotos().cachedIn(viewModelScope)

    fun topicPhotos(topicId: String) =
        pixelRepository.getTopicsPhotos(topicId).cachedIn(viewModelScope)

    private val _liveDataTopics = MutableLiveData<List<TopicsModelItem>>()
    val liveDataTopics: LiveData<List<TopicsModelItem>> = _liveDataTopics
    fun topicsList() {
        viewModelScope.launch {
            if (!networkHelper.hasInternetConnection()) {
                _liveDataTopics.postValue(emptyList())
                return@launch
            }

            try {
                val response = pixelRepository.getTopics()
                val topics = response.toMutableList()
                if (topics.isNotEmpty()) {
                    val currentEvents = topics.firstOrNull { it.title == "Current Events" }
                    if (currentEvents != null) {
                        topics.remove(currentEvents)
                    }
                    topics.add(0, TopicsModelItem(id = "user_type", title = "recommended for you"))
                }
                if (topics.isNotEmpty()) {
                    _liveDataTopics.postValue(getTranslatedPhotos(topics))
                }
            } catch (e: Exception) {
                _liveDataTopics.postValue(emptyList())
                Log.e("TAG", "getAutocomplete: $e")
            }

        }
    }

    private fun getTranslatedPhotos(photos: List<TopicsModelItem>) = buildList {
        photos.forEach {
            val title = getTranslatedTitle(it.title)
            val description = getTranslatedDescription(it.title)

            val model = it.copy(title = title, description = description)
            add(model)
        }
    }.reversed()

    private fun getTranslatedTitle(title: String?): String {
        return when (title) {
            "recommended for you" -> "تازه ترین ها"
            "Food & Drink"        -> "غذا و نوشیدنی"
            "Current Events"      -> "رویدادهای جاری"
            "Wallpapers"          -> "تصاویر پس زمینه"
            "3D Renders"          -> "رندرهای سه بعدی"
            "Textures & Patterns" -> "بافت ها و الگوها"
            "Experimental"        -> "عکس های آزمایشی"
            "Architecture"        -> "معماری"
            "Nature"              -> "طبیعت"
            "Business & Work"     -> "تجارت و کار"
            "Fashion"             -> "مد و فشن"
            "Film"                -> "فیلم"
            "Health & Wellness"   -> "سلامتی و تندرستی"
            "People"              -> "مردم"
            "Interiors"           -> "فضای داخلی"
            "Street Photography"  -> "عکاسی خیابانی"
            "Animals"             -> "حیوانات"
            "Spirituality"        -> "معنویت"
            "Travel"              -> "مسافرت"
            "Arts & Culture"      -> "هنر و فرهنگ"
            "History"             -> "تاریخی"
            else                  -> "unknown"
        }
    }

    private fun getTranslatedDescription(title: String?): String {
        return when (title) {
            "recommended for you" -> "تازه ترین ها"
            "Food & Drink"        -> "هشدار:  اگر گرسنه هستید به این موضوع نگاه نکنید!  \n به دنیای عکاسی آشپزی خوش آمدید \n با عکسهایی از نوشیدنی های سرد، غذاهای خانگی و موارد دیگر"
            "Current Events"      -> "رویدادهای جاری"
            "Wallpapers"          -> "از عکس های حماسی از پهپاد گرفته شده تا لحظات الهام بخش در طبیعت ، بهترین پس زمینه های دسکتاپ و موبایل."
            "3D Renders"          -> "تصاویر 3 بعدی خود را از هر دسته ای که طراحی شده و در قالب تصاویر JPEG رندر شده را مشاهده کنید."
            "Textures & Patterns" -> "چه به دنبال عکاسی ماکرو خیره کننده باشید و چه عکس هایی از اشکال پیچیده معماری به جای درستی آمده اید."
            "Experimental"        -> "از طریق استفاده از بافت های بعید، سوژه های جذاب و فرمت های جدید ، عکاسی این قدرت را دارد که دیدگاه های ما را به چالش بکشد و خلاقیت را به جلو سوق دهد."
            "Architecture"        -> "نمای بیرونی را از سرتاسر جهان کاوش کنید \n از ساختمان های بروتالیستی گرفته تا سازه های مینیمالیستی که به شما قدردانی جدیدی از هنر معماری می دهد."
            "Nature"              -> "بیایید جادوی زمین مادر را جشن بگیریم \n با تصاویری از هر چیزی که سیاره ما ارائه می دهد، از مناظر دریای خیره کننده، آسمان پرستاره، و همه چیز در این بین."
            "Business & Work"     -> "انعکاس واقعیت های محل کار مدرن به اشکال مختلف \n از تصاویر دورکاری و راه اندازی گرفته تا عکس هایی از مهندسان و هنرمندان در حال کار."
            "Fashion"             -> "از عکس های سبک خیابانی گرفته تا عکاسی سرمقاله \n جدیدترین روندهای زیبایی و مد را بیابید."
            "Film"                -> "عکاسی آنالوگ زیبا از گذشته و امروز \n شامل پلاروید، تصاویر سیاه و سفید، پرتره های دانه دار و موارد دیگر."
            "Health & Wellness"   -> "با عکس هایی که همه چیز را از اکتشافات پزشکی جدید و داروهای جایگزین گرفته تا تغذیه سالم و مدیتیشن به نمایش می گذارد، ذهن، بدن و روح سالم را جشن بگیرید."
            "People"              -> "افراد واقعی، اسیر شده اند. عکاسی این قدرت را دارد که دنیای اطراف ما را منعکس کند، به افراد و گروه های موجود در جوامع ما صدا بدهد و مهمتر از همه داستان آنها را بازگو کند."
            "Interiors"           -> "چه یک اتاق خواب آرام باشد یا یک آشپزخانه به هم ریخته ، عکس های فضاهای ما داستان ما را نشان می دهد."
            "Street Photography"  -> "از رفت وآمدهای صبح زود تا شب های نئونی، خیابان های ما به بافت تاریخ مشترک ما تبدیل شده اند. این دسته عکاسی خیابانی را در هر شکلی در بر می گیرد."
            "Animals"             -> "حیات وحش عجیب و غریب، بچه گربه های خانگی و همه چیز در این بین. زیبایی قلمرو حیوانات را از طریق صفحه نمایش خود کشف کنید."
            "Spirituality"        -> "عکاسی توانایی بررسی سوالات بزرگ زندگی را با کاوش در موضوعات عشق، از دست دادن، شفا و ارتباط انسانی دارد."
            "Travel"              -> "شگفتی های پنهان و مقاصد الهام بخش در سراسر جهان را از آسایش خانه خود کشف کنید."
            "Arts & Culture"      -> "فرهنگ روزانه شما \n با عکاسی که بهترین هنر، موسیقی و ادبیات را از سراسر جهان به نمایش می گذارد."
            "History"             -> "عکس های قدرتمند از گذشته \n کاوش در لحظه های تاریخی در طول تاریخ مشترک ما."
            else                  -> "unknown"
        }
    }

    fun onTopicItemClick(topicsModelItem: TopicsModelItem, position: Int) {
        val editor = pref.edit()
        editor.putInt("topic_position_item", position)
        editor.putString("topic_id_item", topicsModelItem.id)
        editor.apply()
    }

    fun getTopicIdAndPosition(): Pair<String, Int> {

        val position = pref.getInt("topic_position_item", liveDataTopics.value?.lastIndex ?: 0)
        val id =
            pref.getString("topic_id_item", TopicsModelItem.Type.USER) ?: TopicsModelItem.Type.USER
        return Pair(id, position)
    }

}