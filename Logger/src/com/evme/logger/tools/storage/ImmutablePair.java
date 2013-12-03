package com.evme.logger.tools.storage;

import java.io.Serializable;

public class ImmutablePair<T, S> implements Serializable
{
	private static final long serialVersionUID = 40;

	public final T element1;

	public final S element2;

	public ImmutablePair()
	{
		element1 = null;
		element2 = null;
	}

	public ImmutablePair(T element1_, S element2_)
	{
		element1 = element1_;
		element2 = element2_;
	}

	@Override
	public boolean equals(Object object_)
	{
		if (object_ instanceof ImmutablePair == false)
		{
			return false;
		}

		Object object1 = ((ImmutablePair<?, ?>)object_).element1;
		Object object2 = ((ImmutablePair<?, ?>)object_).element2;

		return element1.equals(object1) && element2.equals(object2);
	}

	@Override
	public int hashCode()
	{
		return element1.hashCode() << 16 + element2.hashCode();
	}
}
