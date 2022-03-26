package org.koitharu.kotatsu_dl.util

class CliArguments private constructor(
	private val map: HashMap<String, String>,
) : Map<String, String> by map {

	constructor(args: Array<String>) : this(HashMap(args.size)) {
		for (arg in args) {
			if (!arg.startsWith("--")) {
				System.err.println("Wrong argument: %s".format(arg))
				continue
			}
			val name = arg.removePrefix("--").substringBefore('=')
			val value = arg.substringAfter('=', "")
			map[name] = value
		}
	}
}