package io.github.wulkanowy.ui.modules.grade

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.github.wulkanowy.di.scopes.PerChildFragment
import io.github.wulkanowy.di.scopes.PerFragment
import io.github.wulkanowy.ui.base.BaseFragmentPagerAdapter
import io.github.wulkanowy.ui.modules.grade.details.GradeDetailsFragment
import io.github.wulkanowy.ui.modules.grade.statistics.GradeStatisticsFragment
import io.github.wulkanowy.ui.modules.grade.summary.GradeSummaryFragment

@Suppress("unused")
@Module
abstract class GradeModule {

    companion object {

        @PerFragment
        @Provides
        fun provideGradeAdapter(fragment: GradeFragment) = BaseFragmentPagerAdapter(fragment.childFragmentManager)
    }

    @PerChildFragment
    @ContributesAndroidInjector
    abstract fun bindGradeDetailsFragment(): GradeDetailsFragment

    @PerChildFragment
    @ContributesAndroidInjector
    abstract fun binGradeSummaryFragment(): GradeSummaryFragment

    @PerChildFragment
    @ContributesAndroidInjector
    abstract fun binGradeStatisticsFragment(): GradeStatisticsFragment
}
