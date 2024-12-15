package org.koitharu.kotatsu.sync.data

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import org.koitharu.kotatsu.R
import org.koitharu.kotatsu.core.util.ext.ifNullOrEmpty
import javax.inject.Inject

class SyncSettings(
	context: Context,
	private val account: Account?,
) {

	@Inject
	constructor(@ApplicationContext context: Context) : this(
		context,
		AccountManager.get(context)
			.getAccountsByType(context.getString(R.string.account_type_sync))
			.firstOrNull(),
	)

	private val accountManager = AccountManager.get(context)
	private val defaultSyncUrl = context.getString(R.string.sync_url_default)

	@get:WorkerThread
	@set:WorkerThread
	var syncURL: String
		get() = account?.let {
			val result = accountManager.getUserData(it, KEY_SYNC_URL)
			if (!result.startsWith("http://") && !result.startsWith("https://")) {
				return "http://$result"
			}
			return result
		}.ifNullOrEmpty { defaultSyncUrl }
		set(value) {
			account?.let {
				accountManager.setUserData(it, KEY_SYNC_URL, value)
			}
		}

	companion object {

		const val KEY_SYNC_URL = "host"
	}
}