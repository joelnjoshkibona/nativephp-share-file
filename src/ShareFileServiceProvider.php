<?php

namespace Blutrixx\ShareFile;

use Illuminate\Support\ServiceProvider;

/**
 * No PHP of its own: it answers the bridge calls `Native\Mobile\Share` (the Share facade) already makes, Share.File and
 * Share.Url, which NativePHP v4's core no longer implements.
 */
class ShareFileServiceProvider extends ServiceProvider
{
    public function register(): void
    {
        //
    }

    public function boot(): void
    {
        //
    }
}
