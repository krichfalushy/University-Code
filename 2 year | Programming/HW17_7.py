def only_kwargs(f):
    def _only_kwargs(*args, **kwargs):
        if args:
            raise RuntimeError(f"Функція містить позиційні параметри {args}")
        return f(*args, **kwargs)

    return _only_kwargs


@only_kwargs
def function(*args, **kwargs):
    print(args, kwargs)


print(function(x=3, y=5, z=7, u=10))
print(function(1, 2, 3, 4, a=5, b=6, c=7))